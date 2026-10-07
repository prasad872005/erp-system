import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(
    localStorage.getItem("token")
  );

  const [username, setUsername] = useState(
    localStorage.getItem("username")
  );

  const [role, setRole] = useState(
    localStorage.getItem("role")
  );

  const login = (authData) => {
    localStorage.setItem("token", authData.token);
    localStorage.setItem("username", authData.username);
    localStorage.setItem("role", authData.role);

    setToken(authData.token);
    setUsername(authData.username);
    setRole(authData.role);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");

    setToken(null);
    setUsername(null);
    setRole(null);
  };

  const isAuthenticated = !!token;

  return (
    <AuthContext.Provider
      value={{
        token,
        username,
        role,
        isAuthenticated,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};