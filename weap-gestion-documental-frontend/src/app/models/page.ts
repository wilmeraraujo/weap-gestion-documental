export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;  // número de página actual
  size: number;    // tamaño de página
  // otros campos opcionales si usas
}

