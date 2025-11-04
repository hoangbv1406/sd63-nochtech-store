// File: user.response.ts
import { Role } from "../models/role";

export interface UserResponse {
    id: number;
    fullname: string;
    phone_number: string;
    email: string;
    address: string;
    is_active: boolean;
    date_of_birth: Date;
    facebook_account_id: string;
    google_account_id: string;
    role: Role;
    created_at?: Date | null;
}
