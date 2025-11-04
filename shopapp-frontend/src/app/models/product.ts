// File: product.ts
import { ProductImage } from "./product.image";

export interface Product {
  id: number;
  name: string;
  price: number;
  thumbnail: string;
  description: string;
  category_id: number;
  category_name?: string;
  url: string;
  product_images: ProductImage[];
}
