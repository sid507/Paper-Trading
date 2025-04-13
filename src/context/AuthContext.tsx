import React, { createContext, useContext, useState } from 'react';

interface AuthContextType {
  jwt: string | null;
  setJwt: (jwt: string | null) => void;
  isAuthenticated: boolean;
  userName: string | null;
  setUserName: (userName: string | null) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [jwt, setJwt] = useState<string | null>(localStorage.getItem('jwt'));
    const [userName, setUserName] = useState<string | null>(null);

  const handleSetJwt = (newJwt: string | null) => {
    if (newJwt) {
      localStorage.setItem('jwt', newJwt);
    } else {
      localStorage.removeItem('jwt');
    }
    setJwt(newJwt);
  };

  const logout = () => {
    localStorage.removeItem('jwt');
    setJwt(null);
  };

  return (
    <AuthContext.Provider 
      value={{ 
        jwt, 
        setJwt: handleSetJwt, 
        isAuthenticated: !!jwt,
        userName, 
        setUserName: setUserName,
        logout 
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};