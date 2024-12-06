"use client";
import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  useEffect,
} from "react";
import { Cart } from "./cart";
import axios from "axios";
import { useToast } from "@/hooks/use-toast";

// Define the CartContext types
interface CartContextType {
  cart: Cart | null;
  getCart: (userId: number) => Promise<void>;
  addItem: (userId: number, productId: number) => Promise<void>;
  removeItem: (userId: number, productId: number) => Promise<void>;
}

const username = "user";
const password = "user";

// Encode username and password in base64
const basicAuth = `Basic ${btoa(`${username}:${password}`)}`;

// Create the context
const CartContext = createContext<CartContextType | undefined>(undefined);

// Define the CartProvider component
export const CartProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const { toast } = useToast();
  const [cart, setCart] = useState<Cart | null>(null);

  const API_URL = "http://localhost:8083/cart/"; // Adjust to your API base URL

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

  const getCart = async (userId: number) => {
    try {
      const response = await axios.get<Cart>(API_URL + "user/" + userId, {
        headers: { Authorization: basicAuth },
      });
      setCart(response.data);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 404) {
          if (
            String(error.response.data).includes(
              "cart belonging to this userid"
            )
          ) {
            try {
              const createResponse = await axios.post<Cart>(
                API_URL + "user/" + userId,
                {},
                { headers: { Authorization: basicAuth } }
              );
              setCart(createResponse.data);
            } catch (error) {
              handleError(error, "Error fetching cart");
            }
          } else {
            handleError(error, "Error fetching cart");
          }
        } else {
          handleError(error, "Error fetching cart");
        }
      }
    }
  };

  // Add item to the cart
  const addItem = async (userId: number, productId: number) => {
    if (!cart) return;

    try {
      const response = await axios.post<Cart>(
        API_URL + "user/" + userId + "/" + productId + "/add",
        {},
        {
          headers: { Authorization: basicAuth },
        }
      );
      setCart(response.data);
      handleSucces("item added to cart succesfully");
    } catch (error) {
      handleError(error, "error adding item to cart");
    }
  };

  // Remove item from the cart
  const removeItem = async (userId: number, productId: number) => {
    if (!cart) return;

    try {
      const response = await axios.post<Cart>(
        API_URL + "user/" + userId + "/" + productId + "/remove",
        {},
        {
          headers: { Authorization: basicAuth },
        }
      );
      setCart(response.data);
      handleSucces("item removed from cart succesfully");
    } catch (error) {
      handleError(error, "error removing item from cart");
    }
  };

  useEffect(() => {
    getCart(1234);
  }, []);

  return (
    <CartContext.Provider value={{ cart, addItem, removeItem, getCart }}>
      {children}
    </CartContext.Provider>
  );
};

// Custom hook to use the CartContext
export const useCart = (): CartContextType => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within a CartProvider");
  }
  return context;
};
