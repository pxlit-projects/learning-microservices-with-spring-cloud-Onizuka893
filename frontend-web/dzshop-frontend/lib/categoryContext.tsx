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
  const [categories, setCategories] = useState<Category[]>([]);

  const API_URL = "http://localhost:8083/product/category"; // Adjust to your API base URL

  // Fetch all products
  const getCategories = async () => {
    try {
      const response = await axios.get<Category[]>(API_URL);
      setCategories(response.data);
    } catch (error) {
      console.error("Error fetching categories:", error);
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
    } catch (error) {
      console.error("Error creating category:", error);
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
    } catch (error) {
      console.error("Error updating category:", error);
    }
  };

  // Delete a product
  const deleteCategory = async (id: number) => {
    try {
      await axios.delete(`${API_URL}/${id}`, {
        headers: { Authorization: basicAuth },
      });
      setCategories((prev) => prev.filter((product) => product.id !== id));
    } catch (error) {
      console.error("Error deleting category:", error);
    }
  };

  // Effect to load products initially
  useEffect(() => {
    getCategories();
  }, []);

  return (
    <CategoryContext.Provider
      value={{
        categories: categories,
        getCategories: getCategories,
        createCategory: createCategory,
        updateCategory: updateCategory,
        deleteCategory: deleteCategory,
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
