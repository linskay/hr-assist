import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AnimatePresence } from 'framer-motion';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { Layout } from './components/layout/Layout';
import Dashboard from './components/screens/Dashboard';
import InterviewScreen from './components/screens/InterviewScreen';
import ReportScreen from './components/screens/ReportScreen';
import LandingPage from './components/screens/LandingPage';
import LoginPage from './components/auth/LoginPage';
import SettingsPage from './components/screens/SettingsPage';
import NotFoundPage from './components/screens/NotFoundPage';
import CookieConsent from './components/layout/CookieConsent';

function AppRoutes() {
  const location = useLocation();
  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        {/* Public Routes */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />

        {/* Protected Routes */}
        <Route
          path="/app/*"
          element={
            <ProtectedRoute>
              <Layout>
                <Routes>
                  <Route path="dashboard" element={<Dashboard />} />
                  <Route path="interviews" element={<InterviewScreen />} />
                  <Route path="reports" element={<ReportScreen />} />
                  <Route path="settings" element={<SettingsPage />} />
                  {/* Redirect any other /app path to the dashboard */}
                  <Route path="*" element={<Navigate to="/app/dashboard" replace />} />
                </Routes>
              </Layout>
            </ProtectedRoute>
          }
        />

        {/* Fallback for any other top-level route */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </AnimatePresence>
  );
}

function App() {
  return (
    <Router>
      <div className="min-h-screen">
        <AppRoutes />
        <CookieConsent />
        <Toaster
          position="top-right"
          toastOptions={{
            className: 'glass-card text-white',
            duration: 4000,
            style: {
              background: 'rgba(13, 11, 51, 0.5)', // brand-bg with transparency
            },
            success: {
              className: 'glass-card text-brand-highlight-aqua',
              style: {
                borderColor: '#00eaff',
              },
              iconTheme: {
                primary: '#00eaff',
                secondary: '#0d0b33',
              },
            },
            error: {
              className: 'glass-card text-brand-highlight-pink',
               style: {
                borderColor: '#ff00ff',
              },
              iconTheme: {
                primary: '#ff00ff',
                secondary: '#0d0b33',
              },
            },
          }}
        />
      </div>
    </Router>
  );
}

export default App;