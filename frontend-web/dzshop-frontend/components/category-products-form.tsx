"use client";

import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Category } from "@/lib/category";
import { CircleX } from "lucide-react";
import { Product } from "@/lib/product";

interface CategoryProductsFormProps {
  category: Category | null; // Optional, used for editing
  onClick: (categoryId: number, productId: number) => Promise<void>;
}

export function CategoryProductsForm({
  category,
  onClick,
}: CategoryProductsFormProps) {
  const [formData, setFormData] = useState<{
    id: number;
    products: Product[];
  }>({
    id: 0,
    products: [],
  });

  useEffect(() => {
    if (category) {
      setFormData(category);
    }
  }, [category]);

  const handleRemoveProductFromCategory = (
    categoryId: number,
    productId: number
  ) => {
    onClick(categoryId, productId);

    setFormData((prev) => ({
      ...prev,
      products: prev.products.filter((p) => p.id !== productId), // Filter out the product
    }));
  };

  return (
    <div>
      {formData.products.map((p) => (
        <div
          key={p.id}
          className="border rounded-lg p-2 flex justify-between items-center m-2"
        >
          <div>{p.name}</div>
          <Button
            variant={"destructive"}
            onClick={() => handleRemoveProductFromCategory(formData.id, p.id)}
          >
            <CircleX />
          </Button>
        </div>
      ))}
    </div>
  );
}
