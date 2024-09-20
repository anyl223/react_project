// LoginPage.tsx
import React, { useState } from 'react';
import { useRouter } from 'next/router';
import { Box, Button, FormControl, FormLabel, Input, Radio, RadioGroup, Stack } from '@chakra-ui/react';
import { useForm } from 'react-hook-form';
import fetcher from '@/utils/fetcher';
import useUserStore from '@/Store/UsersStore';
import scss from './login.module.scss'; // Import SCSS file
import { ToastContainer, toast } from 'react-toastify';
import { getFingerprint } from '@/utils/fingerprint';

interface FormData {
  username: string;
  password: string;
  role: string;
}

const LoginPage: React.FC = () => {
  const router = useRouter();
  const { register, handleSubmit, setValue, formState: { errors } } = useForm<FormData>();
  const [error, setError] = useState<string>('');
  const setUser = useUserStore((state:any) => state.setUser);

  const onSubmit = async (data: FormData) => {
    try {
      const response = await fetcher("/users/login", "POST",{
        ...data,
        fp:getFingerprint(),
      } );
      if (response && response.token) {
        console.log(response);
        if ((response.user.role === 'user' && response.user.role !== 'user') || (response.user.role === 'admin' && response.user.role !== 'admin')) {
          toast('Invalid user');
          setError('Invalid user');
        } else {
          toast('Login Successful');
          localStorage.setItem('userId', response.user.id.toString());
          setUser({ id: response.user.id, username: response.user.username, role: response.user.role ,jwtTimestamp:response.user.jwtTimestamp ,fp:response.user.fp });
          sessionStorage.setItem('userId', response.user.id.toString());
          setUser({ id: response.user.id, username: response.user.username, role: response.user.role ,jwtTimestamp:response.user.jwtTimestamp ,fp:response.user.fp });
          sessionStorage.setItem('token', response.token);
        
          router.push('/dashboard');
        }
      }
    } catch (error) {
      toast("Invalid username or password");
      setError('Invalid username or password');
    }
  };

  return (
    <div className={scss.logincontainer}>

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
      theme="colored"/>
      <Box maxW="lg" mx="auto" mt={8} mb={8} p={8} height={'max-content'} borderWidth="1px" borderRadius="25px" bg="white">
        <form onSubmit={handleSubmit(onSubmit)}>
          <FormControl id="username" mb={4}>
            <FormLabel>Username</FormLabel>
            <Input type="text" {...register('username', { required: true })} className={scss.logininput} />
            {errors.username && <p style={{ color: 'red' }}>Username is required</p>}
          </FormControl>
          <FormControl id="password" mb={4}>
            <FormLabel>Password</FormLabel>
            <Input type="password" {...register('password', { required: true })} className={scss.logininput} />
            {errors.password && <p style={{ color: 'red' }}>Password is required</p>}
          </FormControl>
          <FormControl as="fieldset" mb={4}>
            <FormLabel as="legend">Role</FormLabel>
            <RadioGroup onChange={(value) => setValue('role', value)} defaultValue="user">
              <Stack direction="row">
                <Radio value="admin">Admin</Radio>
                <Radio value="user">User</Radio>
              </Stack>
            </RadioGroup>
          </FormControl>
          {error && <p style={{ color: 'red' }}>{error}</p>}
          <Button colorScheme="blue" type="submit" mt={4} w="100%" className={scss.loginbutton}>
            Login
          </Button>
        </form>
      </Box>
    </div>
  );
};

export default LoginPage;
