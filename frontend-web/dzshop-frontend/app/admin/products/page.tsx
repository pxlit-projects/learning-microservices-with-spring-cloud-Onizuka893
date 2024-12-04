"use client";

import { useState } from "react";
import { Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { ProductForm } from "@/components/product-form";
import { useProductContext } from "@/lib/productContext";
import { Product } from "@/lib/product";
import { toString } from "@/lib/energy-rating";

export default function ProductsPage() {
  const { products, createProduct, updateProduct, deleteProduct } =
    useProductContext();
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);

  // const handleAddProduct = (newProduct) => {
  //   setProducts([...products, { ...newProduct, id: Date.now() }]);
  // };

  const handleUpdateProduct = (updatedProduct: Partial<Product>) => {
    updateProduct(editingProduct!.id, updatedProduct);
    // setProducts(
    //   products.map((p) => (p.id === updatedProduct.id ? updatedProduct : p))
    // );
    setEditingProduct(null);
  };

  return (
    <div className="w-full">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Products</h1>
        <Dialog>
          <DialogTrigger asChild>
            <Button>
              <Plus className="mr-2 h-4 w-4" /> Add Product
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Add New Product</DialogTitle>
            </DialogHeader>
            <ProductForm product={null} onSubmit={createProduct} />
          </DialogContent>
        </Dialog>
      </div>
      <div className="overflow-x-auto">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Description</TableHead>
              <TableHead>Label</TableHead>
              {/* <TableHead>Category</TableHead> */}
              <TableHead>Price</TableHead>
              <TableHead>Stock</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {products.map((product) => (
              <TableRow key={product.id}>
                <TableCell>{product.name}</TableCell>
                <TableCell>{product.description}</TableCell>
                <TableCell>{toString(product.energyRating)}</TableCell>
                {/* <TableCell>{product.category}</TableCell> */}
                <TableCell>${product.price.toFixed(2)}</TableCell>
                <TableCell>{product.stock}</TableCell>
                <TableCell>
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setEditingProduct(product)}
                      >
                        Edit
                      </Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Edit Product</DialogTitle>
                      </DialogHeader>
                      <ProductForm
                        product={editingProduct}
                        onSubmit={handleUpdateProduct}
                      />
                    </DialogContent>
                  </Dialog>
                  <Button
                    variant="destructive"
                    size="sm"
                    className="ml-2"
                    onClick={() => deleteProduct(product.id)}
                  >
                    Delete
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
