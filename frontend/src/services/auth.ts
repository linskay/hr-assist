import { apiService } from './api';
import { LoginRequest, LoginResponse, User } from '../types/api';

class AuthService {
  private currentUser: User | null = null;

  constructor() {
    // Initialize user from localStorage on service creation
    this.loadUserFromStorage();
  }

  private loadUserFromStorage(): void {
    try {
      const userStr = localStorage.getItem('user');
      if (userStr) {
        this.currentUser = JSON.parse(userStr);
      }
    } catch (error) {
      console.error('Error loading user from storage:', error);
      this.clearAuth();
    }
  }

  private saveUserToStorage(user: User): void {
    try {
      localStorage.setItem('user', JSON.stringify(user));
    } catch (error) {
      console.error('Error saving user to storage:', error);
    }
  }

  private clearAuth(): void {
    this.currentUser = null;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    try {
      const response = await apiService.login(credentials);
      
      // Save tokens and user info
      localStorage.setItem('accessToken', response.accessToken);
      localStorage.setItem('refreshToken', response.refreshToken);
      this.currentUser = response.user;
      this.saveUserToStorage(response.user);
      
      return response;
    } catch (error) {
      this.clearAuth();
      throw error;
    }
  }

  async logout(): Promise<void> {
    try {
      await apiService.logout();
    } catch (error) {
      console.error('Error during logout:', error);
    } finally {
      this.clearAuth();
    }
  }

  async refreshToken(): Promise<boolean> {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        return false;
      }

      const response = await apiService.refreshToken(refreshToken);
      
      // Update tokens
      localStorage.setItem('accessToken', response.accessToken);
      localStorage.setItem('refreshToken', response.refreshToken);
      
      return true;
    } catch (error) {
      console.error('Error refreshing token:', error);
      this.clearAuth();
      return false;
    }
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('accessToken');
    return !!token && !!this.currentUser;
  }

  getCurrentUser(): User | null {
    return this.currentUser;
  }

  hasRole(role: string): boolean {
    return this.currentUser?.role === role;
  }

  hasAnyRole(roles: string[]): boolean {
    return this.currentUser ? roles.includes(this.currentUser.role) : false;
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  // Check if token is expired (basic check)
  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp < currentTime;
    } catch (error) {
      console.error('Error parsing token:', error);
      return true;
    }
  }

  // Auto-refresh token if needed
  async ensureValidToken(): Promise<boolean> {
    if (!this.isAuthenticated()) {
      return false;
    }

    if (this.isTokenExpired()) {
      return await this.refreshToken();
    }

    return true;
  }
}

export const authService = new AuthService();
export default authService;
