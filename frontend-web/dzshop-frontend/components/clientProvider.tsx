"use client";

import { CategoryProvider } from "@/lib/categoryContext";
import { Toaster } from "./ui/toaster";
import { ProductProvider } from "@/lib/productContext";
import { CartProvider } from "@/lib/cartContext";
import { UserProvider } from "@/lib/userContext";

export default function ClientProvider({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <ProductProvider>
        <CategoryProvider>
          <UserProvider>
            <CartProvider>
              <main>{children}</main>
              <Toaster />
            </CartProvider>
          </UserProvider>
        </CategoryProvider>
      </ProductProvider>
    </>
  );
}
