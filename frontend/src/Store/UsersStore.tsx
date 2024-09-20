// useUserStore.ts
import create from 'zustand';
import { createSelectors } from "./create-selectors";

interface User {
  id: string;
  username: string;
  role: string;
}

interface UserStore {
  user: User | null;
  setUser: (user: User | null) => void;
  loadUserFromStorage: () => void;
  
}

const useUserStore = create<UserStore>((set) => ({
  user: null,
  setUser: (user) => {
    set({ user });
    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
      sessionStorage.setItem('user', JSON.stringify(user));
    } else {
      localStorage.removeItem('user');
      sessionStorage.removeItem('user');
    }
  },
  loadUserFromStorage: () => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      set({ user: JSON.parse(storedUser) });
    }
  },
}));

export default useUserStore;
