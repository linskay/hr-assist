import { useEffect, useState } from 'react';
import Dashboard from '@/components/screens/Dashboard';
import InterviewScreen from '@/components/screens/InterviewScreen';
import ReportScreen from '@/components/screens/ReportScreen';

function App() {
  const [hash, setHash] = useState(window.location.hash);

  useEffect(() => {
    const handleHashChange = () => {
      setHash(window.location.hash);
    };
    window.addEventListener('hashchange', handleHashChange);
    return () => {
      window.removeEventListener('hashchange', handleHashChange);
    };
  }, []);

  const renderScreen = () => {
    switch (hash) {
      case '#/interview':
        return <InterviewScreen />;
      case '#/report':
        return <ReportScreen />;
      case '#/dashboard':
      default:
        return <Dashboard />;
    }
  };

  return (
    <main>
      <nav className="p-4 bg-gray-100 dark:bg-gray-800">
        <a href="#/dashboard" className="mr-4 text-blue-500 hover:underline">Dashboard</a>
        <a href="#/interview" className="mr-4 text-blue-500 hover:underline">Interview</a>
        <a href="#/report" className="text-blue-500 hover:underline">Report</a>
      </nav>
      <div className="p-4">
        {renderScreen()}
      </div>
    </main>
  );
}

export default App;
