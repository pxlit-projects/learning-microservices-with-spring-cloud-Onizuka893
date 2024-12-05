"use client";

import React from "react";
import { Button } from "@/components/ui/button";
import { CircleCheck, CirclePlus } from "lucide-react";
import { Product } from "@/lib/product";
import { useCategoryContext } from "@/lib/categoryContext";

interface ProductCategoryFormProps {
  product: Product | null; // Optional, used for editing
}

export function ProductCategoryForm({ product }: ProductCategoryFormProps) {
  const { categories, addProductToCategory } = useCategoryContext();

  const handleAddProductToCategory = (categoryId: number) => {
    addProductToCategory(categoryId, product!.id);
  };

  return (
    <div>
      {categories.map((c) => (
        <div
          key={c.id}
          className="border rounded-lg p-2 flex justify-between items-center m-2"
        >
          <div>{c.name}</div>
          {c.products.find((p) => p.id === product?.id) ? (
            <Button disabled={true} className="bg-green-500">
              <CircleCheck />
            </Button>
          ) : (
            <Button onClick={() => handleAddProductToCategory(c.id)}>
              <CirclePlus />
            </Button>
          )}
        </div>
      ))}
    </div>
  );
}
