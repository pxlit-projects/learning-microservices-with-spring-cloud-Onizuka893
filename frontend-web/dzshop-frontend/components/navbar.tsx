"use client";
import Link from "next/link";
import { ShoppingCart } from "lucide-react";
import { useUserContext } from "@/lib/userContext";

export function Navbar() {
  const { username, clearSession } = useUserContext();

  const logout = () => {
    clearSession();
  };

  return (
    <nav className="bg-primary text-primary-foreground shadow-md">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center">
            <Link href="/shop" className="text-lg font-semibold">
              Duurzaamheids Shop
            </Link>
          </div>
          <div className="flex items-center space-x-4">
            <Link href="/shop" className="hover:text-primary-foreground/80">
              Shop
            </Link>
            {username ? (
              <Link
                href="/shop/cart"
                className="flex items-center hover:text-primary-foreground/80"
              >
                <ShoppingCart className="w-5 h-5 mr-1" />
                Cart
              </Link>
            ) : (
              <Link
                href="/login"
                className="flex items-center hover:text-primary-foreground/80"
              >
                Login
              </Link>
            )}
            {username === "admin" && (
              <Link
                href={"/admin"}
                className="flex items-center hover:text-primary-foreground/80"
              >
                Admin Panel
              </Link>
            )}
            {username && (
              <div
                onClick={logout}
                className="flex cursor-pointer items-center hover:text-primary-foreground/80"
              >
                Logout
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
