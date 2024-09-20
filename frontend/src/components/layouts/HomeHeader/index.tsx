// HomeHeader.tsx
import React, { useState } from 'react';
import { Flex, Text, Button } from '@chakra-ui/react';
import Register from '@/views/Register';
import UserTable from '@/views/Userlist';
import useUserStore from '@/Store/UsersStore';
import Router from 'next/router';
import { ToastContainer, toast } from 'react-toastify';

const HomeHeader: React.FC = () => {
  const user = useUserStore((state) => state.user);
  const setUser = useUserStore((state) => state.setUser);
  const [showRegister, setShowRegister] = useState(false);
  const [showUserTable, setShowUserTable] = useState(false);

  const handleAddUserClick = () => {
    setShowRegister(!showRegister);
  };

  const handleUserListClick = () => {
    setShowUserTable(!showUserTable);
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('userId');
    window.sessionStorage.clear();
    toast("Logged Out Sucessfully!")
    Router.push("/login")
    // Redirect to login page or perform any other logout logic
  };

  return (
    <>
    <ToastContainer/>
      <Flex as="header" align="center" justify="space-between" padding="1rem" backgroundColor="blue.500" color="white">
        <Text fontSize="xl" fontWeight="bold">Dashboard</Text>
        <Flex align="center">
          <Text mr={4} fontFamily={'sans-serif'} fontSize={18}>Logged in as {user?.username}</Text>
          {user?.role === 'admin' && (
            <>
              <Button colorScheme="blue" mr={2} onClick={handleAddUserClick}>
                Add User
              </Button>
            </>
          )}
          <Button colorScheme="blue" onClick={handleLogout}>
            Log Out
          </Button>
        </Flex>
      </Flex>
      {showRegister && <Register onClose={() => setShowRegister(false)} />}
      {showUserTable && <UserTable />}
    </>
  );
};

export default HomeHeader;
