import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { Layout } from './components/layout/Layout';
import Dashboard from './components/screens/Dashboard';
import InterviewScreen from './components/screens/InterviewScreen';
import ReportScreen from './components/screens/ReportScreen';

function App() {
  return (
    <Router>
      <div className="gradient-bg particles min-h-screen">
        <Routes>
          <Route path="/login" element={<ProtectedRoute />} />
          <Route
            path="/*"
            element={
              <ProtectedRoute>
                <Layout>
                  <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/interviews" element={<InterviewScreen />} />
                    <Route path="/reports" element={<ReportScreen />} />
                    <Route path="*" element={<Navigate to="/" replace />} />
                  </Routes>
                </Layout>
              </ProtectedRoute>
            }
          />
        </Routes>
        <Toaster
          position="top-right"
          toastOptions={{
            duration: 4000,
            style: {
              background: 'rgba(0, 0, 0, 0.8)',
              color: '#fff',
              border: '1px solid #00BFFF',
              borderRadius: '8px',
              backdropFilter: 'blur(10px)',
            },
            success: {
              duration: 3000,
              style: {
                background: 'rgba(0, 0, 0, 0.8)',
                color: '#00FF00',
                border: '1px solid #00FF00',
                boxShadow: '0 0 10px #00FF00',
              },
              iconTheme: {
                primary: '#00FF00',
                secondary: '#000',
              },
            },
            error: {
              duration: 5000,
              style: {
                background: 'rgba(0, 0, 0, 0.8)',
                color: '#ff6b6b',
                border: '1px solid #ff6b6b',
                boxShadow: '0 0 10px #ff6b6b',
              },
              iconTheme: {
                primary: '#ff6b6b',
                secondary: '#000',
              },
            },
          }}
        />
      </div>
    </Router>
  );
}

export default App;