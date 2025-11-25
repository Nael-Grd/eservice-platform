/** @type {import('tailwindcss').Config} */
export default {
  // Indique à Tailwind de scanner tous les fichiers .js, .ts, .jsx, .tsx 
  // dans le dossier 'src' pour trouver les classes Tailwind.
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      // Ajout d'une police 'Inter' si vous la chargez (dans index.css par ex)
      fontFamily: {
        'inter': ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
