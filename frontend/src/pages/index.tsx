import React, { useState } from 'react';
import Header from '../components/layouts/Header';
import Footer from '../components/layouts/Footer';
import LoginPage from '@/views/Login';
import Register from "@/views/Register";
import scss from './layout.module.scss'

const Index = () => {
  const [showRegister, setShowRegister] = useState(false);

  const toggleRegister = () => {
    setShowRegister(!showRegister);
  };

  return (
    <div className={scss.layout}>
      <Header onRegisterClick={toggleRegister} />
      <div className={scss.content}>
        {!showRegister ? <LoginPage /> : <Register onClose={toggleRegister} />}
      </div>
      <Footer />
    </div>
  );
};

export default Index;
