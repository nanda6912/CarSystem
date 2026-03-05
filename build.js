const fs = require('fs');
const path = require('path');

// Create public directory if it doesn't exist
if (!fs.existsSync('public')) {
  fs.mkdirSync('public');
  console.log('Created public directory');
}

// Copy all HTML files to public directory
const htmlFiles = fs.readdirSync('.').filter(file => file.endsWith('.html'));

htmlFiles.forEach(file => {
  fs.copyFileSync(file, path.join('public', file));
  console.log(`Copied ${file} to public/`);
});

console.log('Build completed successfully!');
