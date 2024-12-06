"use client";
import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  useEffect,
} from "react";
import Cookies from "js-cookie";

// Context Types
interface UserContextType {
  username: string;
  password: string;
  userId: number | null;
  setUsername: (username: string) => void;
  setPassword: (password: string) => void;
  setUserId: (userId: number | null) => void;
  clearSession: () => void; // To clear cookies
}

// Default Context
const UserContext = createContext<UserContextType | undefined>(undefined);

// Provider Component Props
interface UserProviderProps {
  children: ReactNode;
}

// Provider Component
export const UserProvider: React.FC<UserProviderProps> = ({ children }) => {
  const [username, setUsernameState] = useState<string>("");
  const [password, setPasswordState] = useState<string>("");
  const [userId, setUserIdState] = useState<number | null>(null);

  // Load from cookies on mount
  useEffect(() => {
    const storedUsername = Cookies.get("username") || "";
    const storedPassword = Cookies.get("password") || "";
    const storedUserId = Cookies.get("userId");
    setUsernameState(storedUsername);
    setPasswordState(storedPassword);
    setUserIdState(storedUserId ? parseInt(storedUserId, 10) : null);
  }, []);

  // Save to cookies when state changes
  useEffect(() => {
    if (username) Cookies.set("username", username);
    if (password) Cookies.set("password", password);
    if (userId !== null) Cookies.set("userId", userId.toString());
  }, [username, password, userId]);

  // Update functions that also save to cookies
  const setUsername = (newUsername: string) => {
    setUsernameState(newUsername);
    Cookies.set("username", newUsername);
  };

  const setPassword = (newPassword: string) => {
    setPasswordState(newPassword);
    Cookies.set("password", newPassword);
  };

  const setUserId = (newUserId: number | null) => {
    setUserIdState(newUserId);
    if (newUserId !== null) {
      Cookies.set("userId", newUserId.toString());
    } else {
      Cookies.remove("userId");
    }
  };

  // Clear cookies and context state
  const clearSession = () => {
    setUsernameState("");
    setPasswordState("");
    setUserIdState(null);
    Cookies.remove("username");
    Cookies.remove("password");
    Cookies.remove("userId");
  };

  return (
    <UserContext.Provider
      value={{
        username,
        password,
        userId,
        setUsername,
        setPassword,
        setUserId,
        clearSession,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

// Custom Hook
export const useUserContext = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUserContext must be used within a UserProvider");
  }
  return context;
};
