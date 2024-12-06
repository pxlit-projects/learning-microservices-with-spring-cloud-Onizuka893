"use client";
import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
import axios from "axios";
import { Category } from "./category";
import { useToast } from "@/hooks/use-toast";

const username = "admin";
const password = "admin";

// Encode username and password in base64
const basicAuth = `Basic ${btoa(`${username}:${password}`)}`;

// Context Types
interface CategoryContextType {
  categories: Category[];
  getCategories: () => Promise<void>;
  createCategory: (
    category: Omit<Category, "id" | "products">
  ) => Promise<void>;
  updateCategory: (
    id: number,
    updatedCategory: Partial<Category>
  ) => Promise<void>;
  deleteCategory: (id: number) => Promise<void>;
  deleteProductFromCategory: (
    categoryId: number,
    productId: number
  ) => Promise<void>;
  addProductToCategory: (
    categoryId: number,
    productId: number
  ) => Promise<void>;
  error: string | null; // Error message
  clearError: () => void; // Clear the error
}

// Default Context
const CategoryContext = createContext<CategoryContextType | undefined>(
  undefined
);

// Provider Component Props
interface CategoryProviderProps {
  children: ReactNode;
}

// Provider Component
export const CategoryProvider: React.FC<CategoryProviderProps> = ({
  children,
}) => {
  const { toast } = useToast();
  const [categories, setCategories] = useState<Category[]>([]);
  const [error, setError] = useState<string | null>(null); // State for error messages

  const API_URL = "http://localhost:8083/product/category"; // Adjust to your API base URL

  // Helper to handle errors and set the error state
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

  const handleSucces = (message: string) => {
    toast({ description: message });
  };

  // Fetch all products
  const getCategories = async () => {
    try {
      const response = await axios.get<Category[]>(API_URL);
      setCategories(response.data);
    } catch (error) {
      handleError(error, "Error fetching categories");
    }
  };

  // Create a new product
  const createCategory = async (
    category: Omit<Category, "id" | "products">
  ) => {
    try {
      const response = await axios.post<Category>(API_URL, category, {
        headers: { Authorization: basicAuth },
      });
      setCategories((prev) => [...prev, response.data]);
      handleSucces("Category created!");
    } catch (error) {
      handleError(error, "Error creating category");
    }
  };

  // Update an existing product
  const updateCategory = async (
    id: number,
    updatedProduct: Partial<Category>
  ) => {
    try {
      const response = await axios.put<Category>(
        `${API_URL}/${id}`,
        updatedProduct,
        { headers: { Authorization: basicAuth } }
      );
      setCategories((prev) =>
        prev.map((product) => (product.id === id ? response.data : product))
      );
      handleSucces("Category updated!");
    } catch (error) {
      handleError(error, "Error updating category");
    }
  };

  // Delete a product
  const deleteCategory = async (id: number) => {
    try {
      await axios.delete(`${API_URL}/${id}`, {
        headers: { Authorization: basicAuth },
      });
      setCategories((prev) => prev.filter((product) => product.id !== id));
      handleSucces("Category deleted!");
    } catch (error) {
      handleError(error, "Error deleting category");
    }
  };

  // Add a product to a category
  const addProductToCategory = async (
    categoryId: number,
    productId: number
  ) => {
    try {
      const response = await axios.post(
        `${API_URL}/${categoryId}/products/${productId}`,
        {},
        {
          headers: { Authorization: basicAuth },
        }
      );
      setCategories((prev) =>
        prev.map((category) =>
          category.id === categoryId ? response.data : category
        )
      );
      handleSucces("Product added to category!");
    } catch (error) {
      if (axios.isAxiosError(error)) {
        // Check if the error response is a conflict (HTTP status 409)
        if (error.response?.status === 409) {
          const errorMessage =
            error.response?.data || "Conflict error occurred.";
          handleError(error, errorMessage); // Use the error message from the response
        } else {
          // For other types of errors, use a generic error message
          handleError(error, "Error adding product to category");
        }
      } else {
        handleError(error, "Error adding product to category");
      }
    }
  };

  // Remove a product from a category
  const deleteProductFromCategory = async (
    categoryId: number,
    productId: number
  ) => {
    try {
      const response = await axios.delete(
        `${API_URL}/${categoryId}/products/${productId}`,
        {
          headers: { Authorization: basicAuth },
        }
      );
      setCategories((prev) =>
        prev.map((category) =>
          category.id === categoryId ? response.data : category
        )
      );
      handleSucces("Product removed from category!");
    } catch (error) {
      handleError(error, "Error removing product from category");
    }
  };

  // Effect to load products initially
  useEffect(() => {
    getCategories();
  }, []);

  // Clear error
  const clearError = () => {
    setError(null);
  };

  return (
    <CategoryContext.Provider
      value={{
        categories: categories,
        getCategories: getCategories,
        createCategory: createCategory,
        updateCategory: updateCategory,
        deleteCategory: deleteCategory,
        addProductToCategory: addProductToCategory,
        deleteProductFromCategory: deleteProductFromCategory,
        error: error,
        clearError: clearError,
      }}
    >
      {children}
    </CategoryContext.Provider>
  );
};

// Custom Hook
export const useCategoryContext = () => {
  const context = useContext(CategoryContext);
  if (!context) {
    throw new Error(
      "useCategoryContext must be used within a CategoryProvider"
    );
  }
  return context;
};
