// Dashboard.tsx
import React, { useEffect, useState } from 'react';
import { Box, Text } from '@chakra-ui/react';
import HomeHeader from '@/components/layouts/HomeHeader';
import HomeFooter from '@/components/layouts/Footer';
import Userlist from '@/pages/Userlist';
import Register from '@/views/Register';
import UserForm from '@/views/UserForm';
import { useRouter } from 'next/router';
import fetcher from '@/utils/fetcher';
import useUserStore from '@/Store/UsersStore';
import scss from './dashboard.module.scss'

const Dashboard: React.FC = () => {
  const router = useRouter();
  const user = useUserStore((state) => state.user);
  const setUser = useUserStore((state) => state.setUser);
  const loadUserFromStorage = useUserStore((state) => state.loadUserFromStorage);
  const [showRegister, setShowRegister] = useState(false);

  useEffect(() => {
    loadUserFromStorage();
    const userId = localStorage.getItem('userId');
    if (!userId) {
      router.push('/login');
      return;
    }
    fetchUser(userId);
  }, [router, loadUserFromStorage]);

  const fetchUser = async (userId: string) => {
    try {
      const response = await fetcher(`users/get-user/${userId}`, "GET");
      const userData = response;
      setUser({ id: userData.id.toString(), username: userData.username, role: userData.role });
    } catch (error) {
      console.error('Error fetching user:', error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('userId');
    window.sessionStorage.clear();
    setUser(null);
    router.push('/login');
  };

  const handleShowRegister = () => {
    setShowRegister(true);
  };

  return (
    <>
    <div className={scss.maindiv}>

      <HomeHeader user={user} onLogout={handleLogout} />
      <Box>
        {showRegister && <Register onClose={() => setShowRegister(false)} />}
        {user?.role === 'admin' && <Userlist />}
        {user?.role === 'user' && (
          <Box>
            <Text>Welcome, {user.username}!</Text>
            <Box className="user-form-container">
              <UserForm />
            </Box>
          </Box>
        )}
      </Box>
      <HomeFooter />

    </div>
    </>
  );
};

export default Dashboard;
