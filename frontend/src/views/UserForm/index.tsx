import React from 'react';
import { Button, FormControl, FormErrorMessage, FormLabel, Input, Select, Box, Center } from "@chakra-ui/react";
import fetcher from '@/utils/fetcher';
import { SubmitHandler, useForm } from "react-hook-form";
import router from 'next/router';
import styles from './userform.module.scss'; // Import SCSS file

type PersonalDetail = {
  firstName: string;
  lastName: string;
  gender: string;
  dob: string;
  email: string;
  mobile: string;
};

const Form = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<PersonalDetail>({
    mode: "onBlur",
  });

  const onSubmit: SubmitHandler<PersonalDetail> = async (data) => {
    try {
      const response = await fetcher("/users/Registeruser", "POST", data);
      router.reload();
      console.log("response", response);
      alert("User registered successfully!");
    } catch (error) {
      console.error("Failed to register user:", error);
    }
  };

  return (
    <Center>
      <Box className={styles.formContainer}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <FormControl id="firstName" className={styles.formGroup} isInvalid={!!errors.firstName}>
            <FormLabel>First Name:</FormLabel>
            <Input type="text" {...register("firstName", { required: "First name is required" })} />
            <FormErrorMessage>{errors.firstName && errors.firstName.message}</FormErrorMessage>
          </FormControl>

          <FormControl id="lastName" className={styles.formGroup} isInvalid={!!errors.lastName}>
            <FormLabel>Last Name:</FormLabel>
            <Input type="text" {...register("lastName", { required: "Last name is required" })} />
            <FormErrorMessage>{errors.lastName && errors.lastName.message}</FormErrorMessage>
          </FormControl>

          <FormControl id="gender" className={styles.formGroup}>
            <FormLabel>Gender:</FormLabel>
            <Select {...register("gender")}>
              <option value="male">Male</option>
              <option value="female">Female</option>
              <option value="other">Other</option>
            </Select>
          </FormControl>

          <FormControl id="dob" className={styles.formGroup} isInvalid={!!errors.dob}>
            <FormLabel>Date of Birth:</FormLabel>
            <Input type="date" {...register("dob", { required: "Date of birth is required" })} />
            <FormErrorMessage>{errors.dob && errors.dob.message}</FormErrorMessage>
          </FormControl>

          <FormControl id="email" className={styles.formGroup} isInvalid={!!errors.email}>
            <FormLabel>Email:</FormLabel>
            <Input type="email" {...register("email", { required: "Email is required" })} />
            <FormErrorMessage>{errors.email && errors.email.message}</FormErrorMessage>
          </FormControl>

          <FormControl id="mobile" className={styles.formGroup} isInvalid={!!errors.mobile}>
            <FormLabel>Mobile:</FormLabel>
            <Input type="tel" {...register("mobile", { required: "Mobile is required" })} />
            <FormErrorMessage>{errors.mobile && errors.mobile.message}</FormErrorMessage>
          </FormControl>

          <Button type="submit" colorScheme="blue" className={styles.button}>Register</Button>
        </form>
      </Box>
    </Center>
  );
};

export default Form;
