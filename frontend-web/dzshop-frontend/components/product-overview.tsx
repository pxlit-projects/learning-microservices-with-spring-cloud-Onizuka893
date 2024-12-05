"use client";

import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Product } from "@/lib/product";
import { useProductContext } from "@/lib/productContext";
import { toString } from "@/lib/energy-rating";
import { useCategoryContext } from "@/lib/categoryContext";

export default function ProductOverview() {
  const { products, searchProducts } = useProductContext();
  const { categories } = useCategoryContext();
  const [searchTerm, setSearchTerm] = useState("");
  const [currentCategory, setCurrentCategory] = useState("all");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [energyRating, setEnergyRating] = useState("all");

  const handleTermChange = (term: string) => {
    setSearchTerm(term);
    searchProducts(term, currentCategory, minPrice, maxPrice, energyRating);
  };

  const handleMinPriceChange = (minPrice: string) => {
    setMinPrice(minPrice);
    searchProducts(
      searchTerm,
      currentCategory,
      minPrice,
      maxPrice,
      energyRating
    );
  };

  const handleMaxPriceChange = (maxPrice: string) => {
    setMaxPrice(maxPrice);
    searchProducts(
      searchTerm,
      currentCategory,
      minPrice,
      maxPrice,
      energyRating
    );
  };

  const handleEnergyRatingChange = (rating: string) => {
    setEnergyRating(rating);
    searchProducts(searchTerm, currentCategory, minPrice, maxPrice, rating);
  };

  const handleCategoryChange = (category: string) => {
    setCurrentCategory(category);
    searchProducts(searchTerm, category, minPrice, maxPrice, energyRating);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Product Overview</h1>

      <div className="mb-6">
        <Input
          type="text"
          placeholder="Search products..."
          value={searchTerm}
          onChange={(e) => handleTermChange(e.target.value)}
          className="w-full"
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div>
          <Label htmlFor="minPrice">Min Price</Label>
          <Input
            type="number"
            id="minPrice"
            value={minPrice}
            onChange={(e) => handleMinPriceChange(e.target.value)}
            placeholder="Min price"
          />
        </div>
        <div>
          <Label htmlFor="maxPrice">Max Price</Label>
          <Input
            type="number"
            id="maxPrice"
            value={maxPrice}
            onChange={(e) => handleMaxPriceChange(e.target.value)}
            placeholder="Max price"
          />
        </div>
        <div>
          <Label htmlFor="energyRating">Energy Rating</Label>
          <Select value={energyRating} onValueChange={handleEnergyRatingChange}>
            <SelectTrigger id="energyRating">
              <SelectValue placeholder="Select energy rating" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All</SelectItem>
              <SelectItem value="A++">A++</SelectItem>
              <SelectItem value="A+">A+</SelectItem>
              <SelectItem value="A">A</SelectItem>
              <SelectItem value="B">B</SelectItem>
              <SelectItem value="C">C</SelectItem>
              <SelectItem value="D">D</SelectItem>
              <SelectItem value="E">E</SelectItem>
            </SelectContent>
          </Select>
        </div>
        <div>
          <Label htmlFor="category">Category</Label>
          <Select value={currentCategory} onValueChange={handleCategoryChange}>
            <SelectTrigger id="category">
              <SelectValue placeholder="Select Category" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All</SelectItem>
              {categories.map((c) => (
                <SelectItem key={c.id} value={c.name}>
                  {c.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}

interface ProductCardProps {
  product: Product;
}

function ProductCard({ product }: ProductCardProps) {
  return (
    <div className="border rounded-lg p-4 shadow-sm">
      <h2 className="text-lg font-semibold mb-2">{product.name}</h2>
      <p className="text-gray-600 mb-2">Price: ${product.price}</p>
      <p className="text-gray-600 mb-2">Description: {product.description}</p>
      <p className="text-gray-600 mb-2">
        Energy Rating: {toString(product.energyRating)}
      </p>
      <Button
        onClick={() => alert(`Added ${product.name} to cart`)}
        className="w-full mt-2"
      >
        Add to Cart
      </Button>
    </div>
  );
}
