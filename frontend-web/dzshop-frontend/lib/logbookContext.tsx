"use client";
import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
import axios from "axios";
import { useToast } from "@/hooks/use-toast";
import { LogbookEntry } from "./logbook-entry";

// Context Types
interface LogbookContextType {
  entries: LogbookEntry[];
  getEntries: () => Promise<void>;
}

// Default Context
const LogbookContext = createContext<LogbookContextType | undefined>(undefined);

// Provider Component Props
interface LogbookProviderProps {
  children: ReactNode;
}

// Provider Component
export const LogbookProvider: React.FC<LogbookProviderProps> = ({
  children,
}) => {
  const { toast } = useToast();
  const [entries, setEntries] = useState<LogbookEntry[]>([]);

  const API_URL = "http://localhost:8083/logbook"; // Adjust to your API base URL

  const handleError = (error: unknown, message: string) => {
    if (axios.isAxiosError(error)) {
      toast({
        variant: "destructive",
        title: "Error",
        description: error.response?.data || "An error occurred.",
      });
    } else {
      toast({
        variant: "destructive",
        title: "Error",
        description: message,
      });
    }
  };

  // Fetch all products
  const getEntries = async () => {
    try {
      const response = await axios.get<LogbookEntry[]>(API_URL + "/");
      setEntries(response.data);
    } catch (error) {
      handleError(error, "error fetching products");
    }
  };

  // Effect to load products initially
  useEffect(() => {
    getEntries();
  }, []);

  return (
    <LogbookContext.Provider
      value={{
        entries,
        getEntries,
      }}
    >
      {children}
    </LogbookContext.Provider>
  );
};

// Custom Hook
export const useLogbookContext = () => {
  const context = useContext(LogbookContext);
  if (!context) {
    throw new Error("useLogbookContext must be used within a LogbookProvider");
  }
  return context;
};
