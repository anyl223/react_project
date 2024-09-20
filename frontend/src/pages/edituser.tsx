// components/Form.js
import React, { useEffect, useState } from 'react';
import styles from '/home/rdp/Documents/ALL React Projects/crudapp/frontend/src/styles/Form.module.css'; // Import CSS file
 import fetcher from '@/utils/fetcher';
import { SubmitHandler, useForm } from "react-hook-form";
import router, { useRouter } from 'next/router';

import Footer from '@/components/layouts/Footer';
import Header from '@/components/layouts/Header';



type PersonalDetail = {
    name: string;
    email: string;
   mobile:number;
  };
const EditUser = ({ id }: any) => {
  const router = useRouter();
  // const {id} = router.query;
    
    const[user, setUsers]=useState({
      name:"",
      mobile:"",
      email:"",
    })
    useEffect(() => {
      // fetchUsers()
      const fetchUsers = async () => {
        try{
        const response = await fetcher(`/users/get-user/${id}`, "GET");
        setUsers(response);
        console.log(response);
        }
        catch(error){
          console.error(error);
          

        }
      };
 if (id){
   fetchUsers();
 }
     }, [id]);


   const handleChange= (e: React.ChangeEvent<HTMLInputElement>)=>{
    const {name , value}=e.target;
    setUsers((prevState)=>({
      ...prevState,
      [name]:value,
    }));
   }

   
  const handleSubmit = async(e:React.FormEvent<HTMLFormElement>)=>{
    e.preventDefault();
    try{

      const response=await fetcher(`/users/edituser/${id}`,"PUT",user);
console.log("User Updated",response);
router.reload();
alert("User Updated successfully!");

    }
    catch (error){
      console.error(error);
   
    }
  };
    

    return (
      <>
      {/* <Header3/> */}
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.formGroup}>
                <label htmlFor="firstName">First Name:</label>
                <input type="text" id='name' name='name' value={user.name} onChange={handleChange} className={styles.input} />
            </div>
            
            <div className={styles.formGroup}>
                <label htmlFor="email">Email:</label>
                <input type="email"  id='email' name='email' value={user.email} onChange={handleChange} className={styles.input} />
            </div>
            <div className={styles.formGroup}>
        <label htmlFor="mobile">Mobile:</label>
        <input type="tel"  id='mobile' name='mobile' value={user.mobile} onChange={handleChange}   className={styles.input} /> {/* Registering mobile field */}
      </div>
            <button type="submit" className={styles.button}>Update User</button>
        </form>
        {/* <Footer/> */}
        </>
    );
};

export default EditUser;
