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
import { fromString } from "./energy-rating";
import { useToast } from "@/hooks/use-toast";

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
  searchProducts: (
    searchTerm: string | undefined,
    category: string | undefined,
    minPrice: string | undefined,
    maxPrice: string | undefined,
    energyRating: string | undefined
  ) => Promise<void>;
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
  const { toast } = useToast();
  const [products, setProducts] = useState<Product[]>([]);

  const API_URL = "http://localhost:8083/product"; // Adjust to your API base URL

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
  const getProducts = async () => {
    try {
      const response = await axios.get<Product[]>(API_URL + "/");
      setProducts(response.data);
    } catch (error) {
      handleError(error, "error fetching products");
    }
  };

  const searchProducts = async (
    searchTerm: string | undefined,
    category: string | undefined,
    minPrice: string | undefined,
    maxPrice: string | undefined,
    energyRating: string | undefined
  ) => {
    try {
      // Create a URLSearchParams object to build the query string
      const params = new URLSearchParams();

      // Add query parameters only if they are not undefined
      if (searchTerm) params.append("searchTerm", searchTerm);
      if (category && category !== "all") params.append("category", category);
      if (minPrice !== "0" && minPrice !== "" && minPrice !== undefined)
        params.append("minPrice", minPrice.toString());
      if (maxPrice !== "0" && maxPrice !== "" && maxPrice !== undefined)
        params.append("maxPrice", maxPrice.toString());
      if (energyRating != "all")
        params.append("rating", fromString(energyRating!));

      // Make the GET request with dynamic parameters
      const response = await axios.get<Product[]>(`${API_URL}/search`, {
        params: params,
      });

      // Update state with the fetched products
      setProducts(response.data);
    } catch (error) {
      handleError(error, "error fetching products");
    }
  };

  // Create a new product
  const createProduct = async (product: Omit<Product, "id">) => {
    try {
      const response = await axios.post<Product>(API_URL + "/", product, {
        headers: { Authorization: basicAuth },
      });
      setProducts((prev) => [...prev, response.data]);
      handleSucces(`${product.name} created!`);
    } catch (error) {
      handleError(error, "error creating products");
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
      handleSucces(`${updatedProduct.name} updated!`);
    } catch (error) {
      handleError(error, "error updating products");
    }
  };

  // Delete a product
  const deleteProduct = async (id: number) => {
    try {
      await axios.delete(`${API_URL}/${id}`, {
        headers: { Authorization: basicAuth },
      });
      setProducts((prev) => prev.filter((product) => product.id !== id));
      handleSucces(`product deleted!`);
    } catch (error) {
      handleError(error, "error deleting products");
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
        searchProducts,
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
