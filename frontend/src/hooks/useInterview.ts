import { useState, useEffect, useCallback } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { toast } from 'react-hot-toast';
import apiService from '../services/api';
import { Interview, InterviewCreateRequest, InterviewResults, HeartbeatRequest } from '../types/api';

export const useInterviews = () => {
  return useQuery('interviews', apiService.getInterviews, {
    staleTime: 30000, // 30 seconds
  });
};

export const useInterview = (id: number) => {
  return useQuery(['interview', id], () => apiService.getInterview(id), {
    enabled: !!id,
    staleTime: 10000, // 10 seconds
  });
};

export const useInterviewResults = (id: number) => {
  return useQuery(['interview-results', id], () => apiService.getInterviewResults(id), {
    enabled: !!id,
    staleTime: 30000, // 30 seconds
  });
};

export const useCreateInterview = () => {
  const queryClient = useQueryClient();
  
  return useMutation(apiService.createInterview, {
    onSuccess: () => {
      queryClient.invalidateQueries('interviews');
      toast.success('Интервью создано успешно');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Ошибка создания интервью');
    },
  });
};

export const useStartInterview = () => {
  const queryClient = useQueryClient();
  
  return useMutation(apiService.startInterview, {
    onSuccess: (_, interviewId) => {
      queryClient.invalidateQueries(['interview', interviewId]);
      queryClient.invalidateQueries('interviews');
      toast.success('Интервью запущено');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Ошибка запуска интервью');
    },
  });
};

export const useHeartbeat = (interviewId: number) => {
  const [isActive, setIsActive] = useState(false);
  const [intervalId, setIntervalId] = useState<NodeJS.Timeout | null>(null);

  const sendHeartbeat = useCallback(async (data: HeartbeatRequest) => {
    try {
      await apiService.sendHeartbeat(interviewId, data);
    } catch (error) {
      console.error('Heartbeat failed:', error);
    }
  }, [interviewId]);

  const startHeartbeat = useCallback(() => {
    if (intervalId) return;

    setIsActive(true);
    const id = setInterval(() => {
      const heartbeatData: HeartbeatRequest = {
        timestamp: Date.now(),
        userAgent: navigator.userAgent,
        windowWidth: window.innerWidth,
        windowHeight: window.innerHeight,
        isFullscreen: document.fullscreenElement !== null,
        isVisible: !document.hidden,
        browserEvents: getBrowserEvents(),
      };
      
      sendHeartbeat(heartbeatData);
    }, 5000); // Every 5 seconds

    setIntervalId(id);
  }, [intervalId, sendHeartbeat]);

  const stopHeartbeat = useCallback(() => {
    if (intervalId) {
      clearInterval(intervalId);
      setIntervalId(null);
    }
    setIsActive(false);
  }, [intervalId]);

  useEffect(() => {
    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [intervalId]);

  return {
    isActive,
    startHeartbeat,
    stopHeartbeat,
    sendHeartbeat,
  };
};

// Helper function to collect browser events
const getBrowserEvents = (): string => {
  const events: string[] = [];
  
  // Check for visibility changes
  if (document.hidden) {
    events.push('visibilitychange:hidden');
  }
  
  // Check for focus/blur
  if (!document.hasFocus()) {
    events.push('blur');
  }
  
  // Check for devtools (basic detection)
  if (window.outerHeight - window.innerHeight > 200 || window.outerWidth - window.innerWidth > 200) {
    events.push('devtools_suspected');
  }
  
  return events.join(',');
};

export const useMediaUpload = () => {
  const [uploadProgress, setUploadProgress] = useState<Record<string, number>>({});
  const [isUploading, setIsUploading] = useState(false);

  const uploadChunk = useCallback(async (
    interviewId: number,
    questionId: number,
    file: File,
    chunkIndex: number = 0,
    isFinal: boolean = false
  ) => {
    const uploadKey = `${interviewId}-${questionId}-${chunkIndex}`;
    
    try {
      setIsUploading(true);
      setUploadProgress(prev => ({ ...prev, [uploadKey]: 0 }));

      const response = await apiService.uploadChunk(interviewId, questionId, file, chunkIndex, isFinal);
      
      setUploadProgress(prev => ({ ...prev, [uploadKey]: 100 }));
      
      if (response.success) {
        toast.success('Файл загружен успешно');
      } else {
        toast.error(response.message || 'Ошибка загрузки файла');
      }
      
      return response;
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Ошибка загрузки файла');
      throw error;
    } finally {
      setIsUploading(false);
      // Clear progress after a delay
      setTimeout(() => {
        setUploadProgress(prev => {
          const newProgress = { ...prev };
          delete newProgress[uploadKey];
          return newProgress;
        });
      }, 2000);
    }
  }, []);

  return {
    uploadChunk,
    uploadProgress,
    isUploading,
  };
};

export const useInterviewRecording = (interviewId: number, questionId: number) => {
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [mediaRecorder, setMediaRecorder] = useState<MediaRecorder | null>(null);
  const [audioChunks, setAudioChunks] = useState<Blob[]>([]);
  const { uploadChunk } = useMediaUpload();

  const startRecording = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ 
        audio: true, 
        video: false 
      });
      
      const recorder = new MediaRecorder(stream);
      const chunks: Blob[] = [];
      
      recorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          chunks.push(event.data);
        }
      };
      
      recorder.onstop = async () => {
        const audioBlob = new Blob(chunks, { type: 'audio/wav' });
        const file = new File([audioBlob], `recording-${Date.now()}.wav`, { type: 'audio/wav' });
        
        try {
          await uploadChunk(interviewId, questionId, file, 0, true);
        } catch (error) {
          console.error('Upload failed:', error);
        }
        
        // Stop all tracks
        stream.getTracks().forEach(track => track.stop());
      };
      
      setMediaRecorder(recorder);
      setAudioChunks(chunks);
      recorder.start();
      setIsRecording(true);
      setRecordingTime(0);
      
      // Start timer
      const timer = setInterval(() => {
        setRecordingTime(prev => prev + 1);
      }, 1000);
      
      return timer;
    } catch (error) {
      console.error('Failed to start recording:', error);
      toast.error('Не удалось начать запись. Проверьте разрешения микрофона.');
      throw error;
    }
  }, [interviewId, questionId, uploadChunk]);

  const stopRecording = useCallback(() => {
    if (mediaRecorder && isRecording) {
      mediaRecorder.stop();
      setIsRecording(false);
    }
  }, [mediaRecorder, isRecording]);

  return {
    isRecording,
    recordingTime,
    startRecording,
    stopRecording,
  };
};
