import React from 'react';
import {
  Box,
  Button,
  Text,
  FormControl,
  FormLabel,
  Input,
  RadioGroup,
  Stack,
  Radio,
  Center,
} from '@chakra-ui/react';
import axios from 'axios';
import Swal from 'sweetalert2';
import * as Yup from "yup";


import { SubmitHandler, useForm } from 'react-hook-form';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import fetcher from '@/utils/fetcher';
import { yupResolver } from '@hookform/resolvers/yup';
import scss from './register.module.scss'

interface RegisterProps {
  onClose: () => void;
}

interface FormData {
  username: string;
  email: string;
  password: string;
  role: string;
  secretKey?: string;
}

const validationSchema = Yup.object().shape({
  username: Yup.string().required("Username is required"),
  email: Yup.string().email("Invalid email").required("Email is required"),
  password: Yup.string().required("Password is required"),
  role: Yup.string().required("Role is required"),
  secretKey: Yup.string().when('role', {
    is: (value:any) => value === 'admin',
    then :()=> Yup.string().required("Secret key is required for admin"),
    otherwise:()=> Yup.string().notRequired(),
  }),
});

const Register: React.FC<RegisterProps> = ({ onClose }) => {
  const {
    register,
    setValue,
    getValues,
    handleSubmit,
    watch,
    clearErrors,
    formState: { errors },
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
  });
  const role = watch('role');
  const onSubmit: SubmitHandler<FormData> = async (data) => {
    try {
      
      const response = await fetcher("/users/Registeruser", "POST", 
        data
      );
      console.log("response",response);

      if (response) {
          toast.success("User registered successfully");
          onClose();
      } 
    } catch (error) {
      console.error("Error registering user:", error);
      toast.error('Registration failed. Please try again.');
    }
  };
  // console.log("error", errors);

    // event.preventDefault(); // Prevent default form submission

  return (
    <>
      <ToastContainer
        position="bottom-center"
        autoClose={2000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="colored"
      />

      <Box
        position="fixed"
        top="0"
        left="0"
        width="100%"
        height="100%"
        backgroundColor="rgba(0, 0, 0, 0.5)"
        display="flex"
        justifyContent="center"
        alignItems="center"
        zIndex="999"
      >
        <Box
          bg="white"
          p="4"
          borderRadius="25px"
          boxShadow="md"
          maxWidth="400px"
          width="90%"
          position="relative"
        >
          <Button
            position="absolute"
            top="1rem"
            right="1rem"
            colorScheme="gray"
            variant="ghost"
            onClick={onClose}
          >
            Close
          </Button>
          <Center mb="4">
            <Text fontSize="xl" fontWeight="bold">
              Register User
            </Text>
          </Center>
          <form noValidate onSubmit={handleSubmit(onSubmit)}>
            <FormControl id="username" mb="4">
              <FormLabel>Username</FormLabel>
              <Input
                type="text"
                {...register('username', { required: true })}
              />
              {errors.username && <Text color="red.500">Username is required</Text>}
            </FormControl>
            <FormControl id="email" mb="4">
              <FormLabel>Email address</FormLabel>
              <Input
                type="email"
                {...register('email', { required: true })}
              />
              {errors.email && <Text color="red.500">Email is required</Text>}
            </FormControl>
            <FormControl id="password" mb="4">
              <FormLabel>Password</FormLabel>
              <Input
                type="password"
                {...register('password', { required: true })}
              />
              {errors.password && <Text color="red.500">Password is required</Text>}
            </FormControl>
            <FormControl as="fieldset" mb="4">
              <FormLabel as="legend">Role</FormLabel>
              <RadioGroup
                value={watch('role')}
                onChange={(nextValue: string) => setValue('role', nextValue)}
              >
                <Stack direction="row">
                  <Radio value="user">User</Radio>
                  <Radio value="admin">Admin</Radio>
                </Stack>
              </RadioGroup>
            </FormControl>
            {role === 'admin' && (
              <FormControl id="secretKey" mb="4">
                <FormLabel>Secret Key</FormLabel>
                <Input
                  type="password"
                  {...register('secretKey')}
                />
                {errors.secretKey && <Text color="red.500">{errors.secretKey.message}</Text>}
              </FormControl>
            )}
            <Center>
              <Button colorScheme="blue" type="submit" mt="4">
                Register
              </Button>
            </Center>
          </form>
        </Box>
      </Box>
    </>
  );
};

export default Register;
