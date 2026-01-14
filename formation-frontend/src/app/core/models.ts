export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  roles: string[];
  fullname: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  prenom: string;
  nom: string;
  email: string;
  role: 'ETUDIANT' | 'FORMATEUR';
}

export interface Specialite {
  id: number;
  name: string;
}

export interface Cours {
  id: number;
  code: string;
  title: string;
  specialiteId?: number;
  specialiteName?: string;
}

export interface Note {
  id: number;
  coursId?: number;
  valeur?: number;
  type?: string;
}

export interface EtudiantProfile {
  id: number;
  prenom: string;
  nom: string;
  email: string;
  groupeId?: number;
  groupeNom?: string;
}

export interface FormateurProfile {
  id: number;
  prenom: string;
  nom: string;
  email: string;
} 

export interface Seance {
  id: number;
  heureDebut: string;
  heureFin: string;
  salle: string;
  cours?: {
    id: number;
    title: string;
    code?: string;
  };
}

export interface Groupe {
  id: number;
  nom: string;
  specialiteId?: number;
  specialiteName?: string;
}
