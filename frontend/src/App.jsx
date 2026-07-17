import { useState, useEffect } from 'react';
import Login from './components/Login';
import Chat from './components/Chat';
import { authApi } from './services/api';

function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // Check login state when component mounts
  useEffect(() => {
    const checkAuth = async () => {
      const storedUser = localStorage.getItem('user');
      const storedToken = localStorage.getItem('accessToken');

      if (storedUser && storedToken) {
        try {
          const response = await authApi.getAccount();
          if (response.data) {
            setCurrentUser(response.data);
          } else {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('user');
          }
        } catch (error) {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('user');
        }
      }
      setIsLoading(false);
    };

    checkAuth();
  }, []);

  const handleLogin = (user) => {
    setCurrentUser(user);
  };

  const handleLogout = async () => {
    try {
      await authApi.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Xóa khỏi localStorage bất kể API thành công hay không
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      setCurrentUser(null);
    }
  };

  if (isLoading) {
    return (
      <div className="app-container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <div className="spinner" style={{ width: '40px', height: '40px' }}></div>
      </div>
    );
  }

  return (
    <div className="app-container">
      {currentUser ? (
        <Chat currentUser={currentUser} onLogout={handleLogout} />
      ) : (
        <Login onLogin={handleLogin} />
      )}
    </div>
  );
}

export default App;
