// API Types for HR Assistant

export interface User {
  id: number;
  email: string;
  role: 'ADMIN' | 'HR' | 'INTERVIEWER' | 'REVIEWER';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface Vacancy {
  id: number;
  title: string;
  grade: string;
  description: string;
  requirements: Record<string, number>;
  createdAt: string;
  isActive: boolean;
}

export interface VacancyCreateRequest {
  title: string;
  grade: string;
  description: string;
  requirements: Record<string, number>;
}

export interface Interview {
  id: number;
  vacancyId: number;
  vacancyTitle: string;
  candidateName: string;
  candidateEmail?: string;
  candidatePhone?: string;
  status: 'CREATED' | 'STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'FAILED';
  startedAt?: string;
  finishedAt?: string;
  fraudScore?: number;
  matchingScore?: number;
  consentGiven: boolean;
  consentTimestamp?: string;
  createdAt: string;
}

export interface InterviewCreateRequest {
  vacancyId: number;
  candidateName: string;
  candidateEmail?: string;
  candidatePhone?: string;
}

export interface Question {
  id: number;
  interviewId: number;
  orderIndex: number;
  text: string;
  ttsAudioUrl?: string;
  expectedDurationSeconds?: number;
  questionType: 'STANDARD' | 'LIVENESS_CHECK' | 'VOICE_VERIFICATION' | 'TECHNICAL' | 'BEHAVIORAL';
  createdAt: string;
}

export interface Recording {
  id: number;
  interviewId: number;
  questionId: number;
  type: 'AUDIO' | 'VIDEO' | 'AUDIO_VIDEO';
  fileUrl: string;
  fileSizeBytes?: number;
  durationSeconds?: number;
  asrConfidence?: number;
  chunkIndex?: number;
  isFinalChunk: boolean;
  createdAt: string;
}

export interface Transcript {
  id: number;
  recordingId: number;
  text: string;
  wordsJson?: string;
  asrConfidence?: number;
  language: string;
  processingTimeMs?: number;
  createdAt: string;
}

export interface Analysis {
  id: number;
  interviewId: number;
  detailsJson?: string;
  overallScore?: number;
  competencyScoresJson?: string;
  strengthsJson?: string;
  weaknessesJson?: string;
  recommendation?: string;
  recommendationReason?: string;
  processingTimeMs?: number;
  createdAt: string;
}

export interface Antifraud {
  id: number;
  interviewId: number;
  flagsJson?: string;
  livenessScore?: number;
  faceMatchScore?: number;
  voiceMatchScore?: number;
  textAiScore?: number;
  visibilityEventsCount: number;
  heartbeatGaps: number;
  devtoolsDetected: boolean;
  tabSwitchesCount: number;
  windowBlurCount: number;
  overallFraudScore?: number;
  fraudStatus: 'CLEAN' | 'SUSPICIOUS' | 'FRAUD_DETECTED' | 'MANUAL_REVIEW';
  createdAt: string;
}

export interface InterviewResults {
  interviewId: number;
  candidateName: string;
  vacancyTitle: string;
  completedAt: string;
  matchingScore?: number;
  competencyScores?: Record<string, number>;
  recommendation?: string;
  recommendationReason?: string;
  fraudScore?: number;
  fraudStatus?: string;
  livenessScore?: number;
  faceMatchScore?: number;
  voiceMatchScore?: number;
  textAiScore?: number;
  visibilityEventsCount?: number;
  heartbeatGaps?: number;
  devtoolsDetected?: boolean;
  analysisDetails?: string;
  antifraudDetails?: string;
}

export interface MediaUploadResponse {
  fileUrl: string;
  presignedUrl?: string;
  fileSize?: number;
  contentType?: string;
  uploadId?: string;
  success: boolean;
  message: string;
}

export interface HeartbeatRequest {
  timestamp?: number;
  userAgent?: string;
  windowWidth?: number;
  windowHeight?: number;
  isFullscreen?: boolean;
  isVisible?: boolean;
  browserEvents?: string;
}

export interface AuditLog {
  id: number;
  interviewId?: number;
  userId?: number;
  eventType: string;
  message?: string;
  metaJson?: string;
  ipAddress?: string;
  userAgent?: string;
  createdAt: string;
}

export interface ModelVersion {
  id: number;
  modelName: string;
  version: string;
  modelPath: string;
  metadataJson?: string;
  isActive: boolean;
  loadedAt?: string;
  createdAt: string;
}

// API Response wrappers
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  size: number;
  totalPages: number;
}

// Error types
export interface ApiError {
  message: string;
  code?: string;
  details?: Record<string, any>;
}
