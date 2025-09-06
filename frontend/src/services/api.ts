import axios, { AxiosInstance, AxiosResponse } from 'axios';
import toast from 'react-hot-toast';
import {
  LoginRequest,
  LoginResponse,
  Vacancy,
  VacancyCreateRequest,
  Interview,
  InterviewCreateRequest,
  InterviewResults,
  MediaUploadResponse,
  HeartbeatRequest,
  AuditLog,
  ModelVersion,
  ApiResponse,
  PaginatedResponse
} from '../types/api';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: '/api/v1',
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor for auth token
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor for error handling
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login';
        }
        
        const message = error.response?.data?.message || error.message || 'Произошла ошибка';
        toast.error(message);
        
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.api.post('/auth/login', credentials);
    return response.data;
  }

  async logout(): Promise<void> {
    await this.api.post('/auth/logout');
  }

  async refreshToken(refreshToken: string): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.api.post('/auth/refresh', null, {
      params: { refreshToken }
    });
    return response.data;
  }

  // Vacancy endpoints
  async getVacancies(): Promise<Vacancy[]> {
    const response: AxiosResponse<Vacancy[]> = await this.api.get('/vacancies');
    return response.data;
  }

  async getVacancy(id: number): Promise<Vacancy> {
    const response: AxiosResponse<Vacancy> = await this.api.get(`/vacancies/${id}`);
    return response.data;
  }

  async createVacancy(vacancy: VacancyCreateRequest): Promise<Vacancy> {
    const response: AxiosResponse<Vacancy> = await this.api.post('/vacancies', vacancy);
    return response.data;
  }

  async updateVacancy(id: number, vacancy: Partial<VacancyCreateRequest>): Promise<Vacancy> {
    const response: AxiosResponse<Vacancy> = await this.api.put(`/vacancies/${id}`, vacancy);
    return response.data;
  }

  async deleteVacancy(id: number): Promise<void> {
    await this.api.delete(`/vacancies/${id}`);
  }

  // Interview endpoints
  async getInterviews(): Promise<Interview[]> {
    const response: AxiosResponse<Interview[]> = await this.api.get('/interviews');
    return response.data;
  }

  async getInterview(id: number): Promise<Interview> {
    const response: AxiosResponse<Interview> = await this.api.get(`/interviews/${id}`);
    return response.data;
  }

  async createInterview(interview: InterviewCreateRequest): Promise<Interview> {
    const response: AxiosResponse<Interview> = await this.api.post('/interviews', interview);
    return response.data;
  }

  async startInterview(id: number): Promise<void> {
    await this.api.post(`/interviews/${id}/start`);
  }

  async getInterviewResults(id: number): Promise<InterviewResults> {
    const response: AxiosResponse<InterviewResults> = await this.api.get(`/interviews/${id}/results`);
    return response.data;
  }

  // Media upload endpoints
  async uploadChunk(
    interviewId: number,
    questionId: number,
    file: File,
    chunkIndex: number = 0,
    isFinal: boolean = false
  ): Promise<MediaUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('chunkIndex', chunkIndex.toString());
    formData.append('isFinal', isFinal.toString());

    const response: AxiosResponse<MediaUploadResponse> = await this.api.post(
      `/interviews/${interviewId}/questions/${questionId}/upload-chunk`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  }

  async getMediaUrl(interviewId: number, type: string): Promise<string> {
    const response: AxiosResponse<string> = await this.api.get(`/interviews/${interviewId}/media/${type}`);
    return response.data;
  }

  // Heartbeat endpoint
  async sendHeartbeat(interviewId: number, heartbeat: HeartbeatRequest): Promise<void> {
    await this.api.post(`/interviews/${interviewId}/heartbeat`, heartbeat);
  }

  // Admin endpoints
  async getAuditLogs(eventType?: string, limit: number = 100): Promise<AuditLog[]> {
    const params: any = { limit };
    if (eventType) params.eventType = eventType;
    
    const response: AxiosResponse<AuditLog[]> = await this.api.get('/admin/logs', { params });
    return response.data;
  }

  async getInterviewLogs(interviewId: number): Promise<AuditLog[]> {
    const response: AxiosResponse<AuditLog[]> = await this.api.get(`/admin/logs/${interviewId}`);
    return response.data;
  }

  async reloadModels(): Promise<{ status: string; message: string }> {
    const response: AxiosResponse<{ status: string; message: string }> = await this.api.post('/admin/models/reload');
    return response.data;
  }

  async reloadModel(modelName: string): Promise<{ status: string; message: string }> {
    const response: AxiosResponse<{ status: string; message: string }> = await this.api.post(`/admin/models/reload/${modelName}`);
    return response.data;
  }

  async getModelsStatus(): Promise<Record<string, boolean>> {
    const response: AxiosResponse<Record<string, boolean>> = await this.api.get('/admin/models/status');
    return response.data;
  }

  async startTraining(): Promise<{ status: string; message: string }> {
    const response: AxiosResponse<{ status: string; message: string }> = await this.api.post('/admin/ml/train');
    return response.data;
  }

  async deleteInterviewData(interviewId: number): Promise<{ status: string; message: string }> {
    const response: AxiosResponse<{ status: string; message: string }> = await this.api.delete(`/admin/data/${interviewId}`);
    return response.data;
  }

  async getSystemHealth(): Promise<Record<string, any>> {
    const response: AxiosResponse<Record<string, any>> = await this.api.get('/admin/system/health');
    return response.data;
  }
}

export const apiService = new ApiService();
export default apiService;
