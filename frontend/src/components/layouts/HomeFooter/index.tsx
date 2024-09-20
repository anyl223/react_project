import React from 'react';
import { Flex, Text } from '@chakra-ui/react';
import scss from './homefooter.module.scss'

const Footer: React.FC = () => {
  return (
    <Flex className={scss.footer}
      as="footer"
      align="center"
      justify="center"
      padding="1rem"
      backgroundColor="blue.500"
    >
      <Text fontSize="sm">
        © {new Date().getFullYear()} My Chakra UI App. All rights reserved.
      </Text>
    </Flex>
  );
};

export default Footer;
