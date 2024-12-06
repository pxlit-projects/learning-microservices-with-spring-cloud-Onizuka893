"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Product } from "@/lib/product";
import { ComboboxDemo } from "./rating-combobox";
import { getAllEnergyRatings } from "@/lib/energy-rating";

interface ProductFormProps {
  product: Product | null; // Optional, used for editing
  onSubmit: (data: Omit<Product, "id">) => void; // Function to handle form submission
}

export function ProductForm({ product, onSubmit }: ProductFormProps) {
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    energyRating: "",
    price: "",
    stock: "",
  });

  // Pre-fill form when editing an existing product
  useEffect(() => {
    if (product) {
      setFormData({
        name: product.name || "",
        description: product.description || "",
        energyRating: product.energyRating.toString() || "",
        price: product.price.toString() || "",
        stock: product.stock.toString() || "",
      });
    }
  }, [product]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleRatingSelect = (rating: string) => {
    console.log("changed to" + rating);
    setFormData((d) => ({ ...d, energyRating: rating }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit({
      name: formData.name,
      description: formData.description,
      energyRating: formData.energyRating,
      price: parseFloat(formData.price),
      stock: parseInt(formData.stock, 10),
    });
    setFormData({
      name: "",
      description: "",
      energyRating: "",
      price: "",
      stock: "",
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <Label htmlFor="name">Name</Label>
        <Input
          id="name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <Label htmlFor="description">Description</Label>
        <Input
          id="description"
          name="description"
          value={formData.description}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <Label htmlFor="energyRating">Energy Rating</Label>
        <ComboboxDemo
          items={getAllEnergyRatings()}
          initialValue={formData.energyRating}
          onChangeValue={handleRatingSelect}
        />
      </div>
      <div>
        <Label htmlFor="price">Price</Label>
        <Input
          id="price"
          name="price"
          type="number"
          step="0.01"
          value={formData.price}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <Label htmlFor="stock">Stock</Label>
        <Input
          id="stock"
          name="stock"
          type="number"
          value={formData.stock}
          onChange={handleChange}
          required
        />
      </div>
      <Button type="submit">{product ? "Update" : "Add"} Product</Button>
    </form>
  );
}
