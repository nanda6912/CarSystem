const fs = require('fs');
const path = require('path');

// Create public directory if it doesn't exist
if (!fs.existsSync('public')) {
  fs.mkdirSync('public');
  console.log('Created public directory');
}

// Copy all HTML files to both public and root directories
const htmlFiles = fs.readdirSync('.').filter(file => file.endsWith('.html'));

htmlFiles.forEach(file => {
  // Copy to public directory
  fs.copyFileSync(file, path.join('public', file));
  console.log(`Copied ${file} to public/`);
  
  // Also copy to root directory (backup)
  fs.copyFileSync(file, path.join('.', file + '.deployed'));
  console.log(`Copied ${file} to root as ${file}.deployed`);
});

console.log('Build completed successfully!');
