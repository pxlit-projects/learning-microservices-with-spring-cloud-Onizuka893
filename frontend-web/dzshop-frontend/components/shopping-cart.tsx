"use client";

import { Plus, Minus, ShoppingCartIcon } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useCart } from "@/lib/cartContext";
import { useUserContext } from "@/lib/userContext";

export default function ShoppingCart() {
  const { cart, addItem, removeItem } = useCart();
  const { userId } = useUserContext();

  if (!cart) return <div>Login first</div>;

  if (!userId) return <div>Login first</div>;

  return (
    <div className="container mx-auto p-4 max-w-2xl">
      <h1 className="text-2xl font-bold mb-4">Your Shopping Cart</h1>
      {cart.items.length === 0 ? (
        <p>Your cart is empty.</p>
      ) : (
        <>
          <ul className="space-y-4">
            {cart.items
              .slice() // Create a shallow copy to avoid mutating the original array
              .sort((a, b) => a.productName.localeCompare(b.productName)) // Sort alphabetically
              .map((item) => (
                <li
                  key={item.productId}
                  className="flex items-center justify-between border-b pb-2"
                >
                  <div>
                    <h2 className="font-semibold">{item.productName}</h2>
                    <p className="text-sm text-gray-500">
                      ${item.price.toFixed(2)}
                    </p>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={() => removeItem(userId, item.productId)}
                      aria-label={`Decrease quantity of ${item.productName}`}
                    >
                      <Minus className="h-4 w-4" />
                    </Button>
                    <span className="w-8 text-center">{item.quantity}</span>
                    <Button
                      variant="outline"
                      size="icon"
                      onClick={() => addItem(userId, item.productId)}
                      aria-label={`Increase quantity of ${item.productName}`}
                    >
                      <Plus className="h-4 w-4" />
                    </Button>
                  </div>
                </li>
              ))}
          </ul>
          <div className="mt-4 flex justify-between items-center">
            <p className="text-xl font-semibold">
              Total: ${cart.total.toFixed(2)}
            </p>
            <Button onClick={() => alert("Proceeding to checkout...")}>
              <ShoppingCartIcon className="mr-2 h-4 w-4" /> Checkout
            </Button>
          </div>
        </>
      )}
    </div>
  );
}
