import React from 'react';
import { Flex, Text } from '@chakra-ui/react';
import scss from './footer.module.scss'

const HomeFooter: React.FC = () => {
  return (
    <Flex className={scss.footer}
      as="footer"
      align="center"
      justify="center"
      padding="1rem"
      backgroundColor="blue.800"
    >
      <Text fontSize="sm">
       {new Date().getFullYear()} BISAG-N
      </Text>
    </Flex>
  );
};

export default HomeFooter;
