import { useState } from 'react';
import Link from 'next/link';
import { Modal, ModalOverlay, ModalContent, ModalHeader, ModalFooter, ModalBody, ModalCloseButton, Button, FormControl, FormLabel, Input } from '@chakra-ui/react';
import Form from '@/pages/Form';

const Header2 = () => {
  const [isOpen, setIsOpen] = useState(false);

  const onClose = () => setIsOpen(false);
  const onOpen = () => setIsOpen(true);

  return (
    <header className='header-container'>
      <h1 className='logo'>USER LIST</h1>
      <nav>
        <ul className="nav-links">
          {/* <Link href="/Userlist"><a>User List</a></Link> */}
          {/* <li><a href="#">About</a></li>
          <li><a href="#">Services</a></li>
          <li><a href="#">Contact</a></li> */}
        </ul>
        {/* Add button for adding user */}
        {/* <Link legacyBehavior href="/Form">
          <a  onClick={onOpen}>Add User</a>
        </Link> */}
        <Button
              onClick={() => onOpen()}
              style={{
                backgroundColor: "#4caf50",
                color: "#fff",
                border: "none",
                padding: "0.50rem 1rem",
                borderRadius: "20px",
                cursor: "pointer",
                transition: "background-color 0.3s ease",
              }}
            >
              Add User
            </Button>
        {/* Optionally, you can use Link to navigate to another page for adding user */}
        {/* <Link href="/add-user"><a>Add User</a></Link> */}
      </nav>
      <Modal
       
        isOpen={isOpen}
        onClose={onClose}
      >
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Register User</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <FormControl>
             <Form/>
            </FormControl>
{/* 
            <FormControl mt={4}>
              <FormLabel>Last name</FormLabel>
              <Input placeholder='Last name' />
            </FormControl> */}
          </ModalBody>

         
        </ModalContent>
      </Modal>
    </header>
  );
};

export default Header2;
