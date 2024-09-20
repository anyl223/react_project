// Header.tsx
import React from 'react';
import { Flex, Text, Button } from '@chakra-ui/react';

interface HeaderProps {
  onRegisterClick: () => void;
}

const Header: React.FC<HeaderProps> = ({ onRegisterClick }) => {
  return (
    <Flex
      as="header"
      align="center"
      justify="space-between"
      padding="1rem"
      backgroundColor="blue.800"
      color="white"
    >
      <Text fontSize="xl" fontWeight="bold">
        Login Page
      </Text>
      <Flex align="center">
        {/* <Button colorScheme="blue" mr={2}>
          Login
        </Button> */}
        <Button colorScheme="blue" onClick={onRegisterClick}>
          Register
        </Button>
      </Flex>
    </Flex>
  );
};

export default Header;
