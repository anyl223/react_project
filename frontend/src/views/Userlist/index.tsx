import { useState, useEffect } from "react";
import axios from "axios";
import {
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalFooter,
  ModalBody,
  ModalCloseButton,
  Button,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
} from "@chakra-ui/react";
import scss from "./userlist.module.scss";
import fetcher from "@/utils/fetcher";

interface User {
  id: number;
  username: string;
  email: string;
  password: string;
}

const UserTable = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await fetcher("/users/userlist", "POST");
      console.log("response", response);

      setUsers(response.result);
    } catch (error) {
      console.error("Failed to fetch users:", error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      const response = await axios.delete(
        `http://localhost:8080/users/delete-user/${id}`
      );
      console.log("User deleted successfully:", response);
      // After successful deletion, you may want to fetch the updated user list
      fetchUsers();
    } catch (error) {
      console.error("Failed to delete user:", error);
    }
  };

  const handleEdit = (id: number) => {
    setSelectedUserId(id);
    setIsOpen(true);
  };

  const handleCloseModal = () => {
    setSelectedUserId(null);
    setIsOpen(false);
  };

  return (
    <div
      className={
        isOpen ? `${scss.blurBackground} ${scss.centered}` : scss.centered
      }
    >
      <div className={scss.card}>
        <table className={scss.usertable}>
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Password</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>{user.password}</td>
                <td>
                  <Button
                    onClick={() => handleEdit(user.id)}
                    className={scss.editbtn}
                  >
                    Edit
                  </Button>
                  <button
                    className="delete-btn"
                    onClick={() => handleDelete(user.id)}
                    style={{
                      backgroundColor: "#f44336",
                      color: "#fff",
                      border: "none",
                      padding: "0.50rem 1rem",
                      borderRadius: "20px",
                      cursor: "pointer",
                      transition: "background-color 0.3s ease",
                    }}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal isOpen={isOpen} onClose={handleCloseModal} size="xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Edit User</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Table variant="striped" colorScheme="blue">
              <Thead>
                <Tr>
                  <Th>Name</Th>
                  <Th>Email</Th>
                  <Th>Password</Th>
                </Tr>
              </Thead>
              <Tbody>
                {users.map((user) => (
                  <Tr key={user.id}>
                    <Td>{user.username}</Td>
                    <Td>{user.email}</Td>
                    <Td>{user.password}</Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </ModalBody>
          <ModalFooter>
            <Button colorScheme="blue" onClick={handleCloseModal}>
              Close
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </div>
  );
};

export default UserTable;
