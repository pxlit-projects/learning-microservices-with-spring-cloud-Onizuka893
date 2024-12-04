"use client";
import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
import axios from "axios";
import { Product } from "./product";

const username = "admin";
const password = "admin";

// Encode username and password in base64
const basicAuth = `Basic ${btoa(`${username}:${password}`)}`;

// Context Types
interface ProductContextType {
  products: Product[];
  getProducts: () => Promise<void>;
  createProduct: (product: Omit<Product, "id">) => Promise<void>;
  updateProduct: (
    id: number,
    updatedProduct: Partial<Product>
  ) => Promise<void>;
  deleteProduct: (id: number) => Promise<void>;
}

// Default Context
const ProductContext = createContext<ProductContextType | undefined>(undefined);

// Provider Component Props
interface ProductProviderProps {
  children: ReactNode;
}

// Provider Component
export const ProductProvider: React.FC<ProductProviderProps> = ({
  children,
}) => {
  const [products, setProducts] = useState<Product[]>([]);

  const API_URL = "http://localhost:8083/product"; // Adjust to your API base URL

  // Fetch all products
  const getProducts = async () => {
    try {
      const response = await axios.get<Product[]>(API_URL + "/");
      setProducts(response.data);
    } catch (error) {
      console.error("Error fetching products:", error);
    }
  };

  // Create a new product
  const createProduct = async (product: Omit<Product, "id">) => {
    try {
      const response = await axios.post<Product>(API_URL + "/", product, {
        headers: { Authorization: basicAuth },
      });
      setProducts((prev) => [...prev, response.data]);
    } catch (error) {
      console.error("Error creating product:", error);
    }
  };

  // Update an existing product
  const updateProduct = async (
    id: number,
    updatedProduct: Partial<Product>
  ) => {
    try {
      const response = await axios.put<Product>(
        `${API_URL}/${id}`,
        updatedProduct,
        { headers: { Authorization: basicAuth } }
      );
      setProducts((prev) =>
        prev.map((product) => (product.id === id ? response.data : product))
      );
    } catch (error) {
      console.error("Error updating product:", error);
    }
  };

  // Delete a product
  const deleteProduct = async (id: number) => {
    try {
      await axios.delete(`${API_URL}/${id}`, {
        headers: { Authorization: basicAuth },
      });
      setProducts((prev) => prev.filter((product) => product.id !== id));
    } catch (error) {
      console.error("Error deleting product:", error);
    }
  };

  // Effect to load products initially
  useEffect(() => {
    getProducts();
  }, []);

  return (
    <ProductContext.Provider
      value={{
        products,
        getProducts,
        createProduct,
        updateProduct,
        deleteProduct,
      }}
    >
      {children}
    </ProductContext.Provider>
  );
};

// Custom Hook
export const useProductContext = () => {
  const context = useContext(ProductContext);
  if (!context) {
    throw new Error("useProductContext must be used within a ProductProvider");
  }
  return context;
};
