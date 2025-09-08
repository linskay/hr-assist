import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AnimatePresence } from 'framer-motion';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { Layout } from './components/layout/Layout';
import Dashboard from './components/screens/Dashboard';
import InterviewScreen from './components/screens/InterviewScreen';
import ReportScreen from './components/screens/ReportScreen';
import LoginPage from './components/auth/LoginPage';
import SettingsPage from './components/screens/SettingsPage';
import NotFound from './components/screens/NotFound';
import CookieConsent from './components/layout/CookieConsent';

function AppRoutes() {
  const location = useLocation();
  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        {/* Public Routes */}
        <Route path="/" element={<Navigate to="/app/dashboard" replace />} />
        <Route path="/login" element={<LoginPage />} />

        {/* Protected Routes */}
        <Route
          path="/app/*"
          element={
            <ProtectedRoute>
              <Layout>
                <Routes>
                  <Route path="dashboard" element={<Dashboard />} />
                  <Route path="interviews/:id" element={<InterviewScreen />} />
                  <Route path="reports/:id" element={<ReportScreen />} />
                  <Route path="settings" element={<SettingsPage />} />
                  <Route path="*" element={<Navigate to="/app/dashboard" replace />} />
                </Routes>
              </Layout>
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<NotFound />} />
      </Routes>
    </AnimatePresence>
  );
}

function App() {
  return (
    <Router>
      <div className="min-h-screen gradient-bg particles">
        <AppRoutes />
        <CookieConsent />
        <Toaster
          position="top-right"
          toastOptions={{
            className: 'glass-card text-white',
            duration: 4000,
            style: {
              background: 'rgba(13, 11, 51, 0.5)',
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