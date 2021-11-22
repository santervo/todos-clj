const colors = require("tailwindcss/colors");

module.exports = {
  mode: "jit",
  purge:
    // in prod look at shadow-cljs output file in dev look at runtime,
    // which will change files that are actually compiled; postcss watch should be a whole lot faster
    process.env.NODE_ENV == "production"
      ? ["./public/js/main.js"]
      : ["./public/js/cljs-runtime/*.js"],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {},
  },
  variants: {
    extend: {},
  },
  plugins: [],
};
