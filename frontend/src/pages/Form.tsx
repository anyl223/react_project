// components/Form.js
import React, { useState } from 'react';
import styles from '/home/rdp/Documents/ALL React Projects/crudapp/frontend/src/styles/Form.module.css'; // Import CSS file
 import fetcher from '@/utils/fetcher';
import { SubmitHandler, useForm } from "react-hook-form";
import router from 'next/router';


type PersonalDetail = {
    name: string;
    email: string;
   mobile:number;
  };
const Form = () => {
    const [formData, setFormData] = useState({
        
    });

  
    const {
        register,
        handleSubmit,
        getValues,
        formState: { errors },
      } = useForm<PersonalDetail>({
        mode: "onChange",
      });
      const onSubmit: SubmitHandler<PersonalDetail> = async (data) => {
     
            const response = await fetcher("/users/Registeruser", "POST", data);
          router.reload ();
            console.log("response", response);
            alert("User registered successfully!");
       
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit(onSubmit)}>
            <div className={styles.formGroup}>
                <label htmlFor="firstName">First Name:</label>
                <input type="text" {...register("name")} className={styles.input} />
            </div>
            
            <div className={styles.formGroup}>
                <label htmlFor="email">Email:</label>
                <input type="email" {...register("email")} className={styles.input} />
            </div>
            <div className={styles.formGroup}>
        <label htmlFor="mobile">Mobile:</label>
        <input type="tel" {...register("mobile")} className={styles.input} /> {/* Registering mobile field */}
      </div>
            <button type="submit" className={styles.button}>Register</button>
        </form>
    );
};

export default Form;
