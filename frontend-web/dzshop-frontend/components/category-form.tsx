"use client";

import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Category } from "@/lib/category";

interface CategoryFormProps {
  category: Category | null; // Optional, used for editing
  onSubmit: (data: Omit<Category, "id" | "products">) => void; // Function to handle form submission
}

export function CategoryForm({ category, onSubmit }: CategoryFormProps) {
  const [formData, setFormData] = useState({
    name: "",
  });

  useEffect(() => {
    if (category) {
      setFormData(category);
    }
  }, [category]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
    setFormData({ name: "" });
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
      <Button type="submit">{category ? "Update" : "Add"} Category</Button>
    </form>
  );
}
