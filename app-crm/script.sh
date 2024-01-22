

npm run build

# Copy the build artifacts to the Spring Boot static directory
cp -R dist/* /Users/ioannisniozas/IdeaProjects/newstracker/newstracker/verse/src/main/resources/static/

# Navigate back to the root directory
# cd path/to/your/project/root

# # Add and commit the changes
# # git add .
# # git commit -m "Update frontend build artifacts"
# # git push origin main