import os
import shutil
import re

SOURCE_BASE = r"c:\Users\subra\Desktop\Revplaystreaming\RevPlay-MusicPlatform\src\main\java\com\revplay\musicplatform"
DEST_BASE = r"c:\Users\subra\Desktop\Revplaystreaming"

# Mapping monolith module folders to microservices and their base packages
MAPPING = {
    "catalog": ("revplay-catalog-service", "com.revplay.catalogservice"),
    "artist": ("revplay-catalog-service", "com.revplay.catalogservice"),
    "ai": ("revplay-catalog-service", "com.revplay.catalogservice"),
    
    "playlist": ("revplay-playlist-service", "com.revplay.playlistservice"),
    "systemplaylist": ("revplay-playlist-service", "com.revplay.playlistservice"),
    
    "playback": ("revplay-playback-service", "com.revplay.playbackservice"),
    "download": ("revplay-playback-service", "com.revplay.playbackservice"),
    
    "analytics": ("revplay-analytics-service", "com.revplay.analyticsservice"),
    "ads": ("revplay-analytics-service", "com.revplay.analyticsservice"),
}

# The monolith imports look like: import com.revplay.musicplatform.catalog.entity.Album;
# We need to map them dynamically.
IMPORT_MAPPINGS = {
    "com.revplay.musicplatform.catalog": "com.revplay.catalogservice",
    "com.revplay.musicplatform.artist": "com.revplay.catalogservice",
    "com.revplay.musicplatform.ai": "com.revplay.catalogservice",
    
    "com.revplay.musicplatform.playlist": "com.revplay.playlistservice",
    "com.revplay.musicplatform.systemplaylist": "com.revplay.playlistservice",
    
    "com.revplay.musicplatform.playback": "com.revplay.playbackservice",
    "com.revplay.musicplatform.download": "com.revplay.playbackservice",
    
    "com.revplay.musicplatform.analytics": "com.revplay.analyticsservice",
    "com.revplay.musicplatform.ads": "com.revplay.analyticsservice",
    
    "com.revplay.musicplatform.user": "com.revplay.userservice",
    "com.revplay.musicplatform.common": "com.revplay.{service_name}.common", # Handled specially
    "com.revplay.musicplatform.security": "com.revplay.{service_name}.security"
}

def process_file(source_path, dest_path, target_pkg_base, service_name_no_hyphen):
    # Don't overwrite if it already exists to preserve manual fixes (like our User service work)
    if os.path.exists(dest_path):
        return

    os.makedirs(os.path.dirname(dest_path), exist_ok=True)
    
    with open(source_path, 'r', encoding='utf-8') as f:
        content = f.read()
        
    # 1. Update package declaration
    # e.g. package com.revplay.musicplatform.catalog.controller; -> package com.revplay.catalogservice.controller;
    content = re.sub(
        r'package\s+com\.revplay\.musicplatform\.[a-z]+(.*?);',
        f'package {target_pkg_base}\\1;',
        content
    )
    
    # 2. Update imports
    for old_prefix, new_prefix in IMPORT_MAPPINGS.items():
        if "{service_name}" in new_prefix:
            actual_prefix = new_prefix.format(service_name=service_name_no_hyphen)
            content = content.replace(old_prefix, actual_prefix)
        else:
            content = content.replace(old_prefix, new_prefix)
            
    # Fix self-referential common imports that don't match the new prefix structure perfectly
    # e.g. if the file is IN common, its package is com.revplay.catalogservice.common
    
    with open(dest_path, 'w', encoding='utf-8') as f:
        f.write(content)

def main():
    print("Starting module porting...")
    for folder in os.listdir(SOURCE_BASE):
        source_dir = os.path.join(SOURCE_BASE, folder)
        if not os.path.isdir(source_dir):
            continue
            
        if folder in MAPPING:
            target_service, target_pkg_base = MAPPING[folder]
            print(f"Porting {folder} to {target_service}...")
            
            # The destination base for Java source files
            service_src_base = os.path.join(DEST_BASE, target_service, "src", "main", "java", *target_pkg_base.split('.'))
            service_name_no_hyphen = target_service.split('-')[1] + "service" # e.g. catalogservice
            
            for root, _, files in os.walk(source_dir):
                for file in files:
                    if file.endswith('.java'):
                        source_file = os.path.join(root, file)
                        # Calculate relative path from the module folder
                        rel_path = os.path.relpath(source_file, source_dir)
                        dest_file = os.path.join(service_src_base, rel_path)
                        
                        process_file(source_file, dest_file, target_pkg_base, service_name_no_hyphen)
                        
    # Now port Common, Security, and Exception to all mapped microservices if they don't exist
    common_source = os.path.join(SOURCE_BASE, "common")
    security_source = os.path.join(SOURCE_BASE, "security")
    exception_source = os.path.join(SOURCE_BASE, "exception")
    
    target_services = set(target_service for target_service, _ in MAPPING.values())
    for target_service in target_services:
        target_pkg_base = f"com.revplay.{target_service.split('-')[1]}service"
        service_name_no_hyphen = target_service.split('-')[1] + "service"
        
        # Copy Common
        if os.path.exists(common_source):
            for root, _, files in os.walk(common_source):
                for file in files:
                    if file.endswith('.java'):
                        source_file = os.path.join(root, file)
                        rel_path = os.path.relpath(source_file, os.path.join(SOURCE_BASE)) # relative to musicplatform
                        dest_file = os.path.join(DEST_BASE, target_service, "src", "main", "java", target_pkg_base.replace('.', os.sep), rel_path.split(os.sep, 1)[1])
                        process_file(source_file, dest_file, target_pkg_base, service_name_no_hyphen)

        # Copy Security
        if os.path.exists(security_source):
            for root, _, files in os.walk(security_source):
                for file in files:
                    if file.endswith('.java'):
                        source_file = os.path.join(root, file)
                        rel_path = os.path.relpath(source_file, os.path.join(SOURCE_BASE)) # relative to musicplatform
                        dest_file = os.path.join(DEST_BASE, target_service, "src", "main", "java", target_pkg_base.replace('.', os.sep), rel_path.split(os.sep, 1)[1])
                        process_file(source_file, dest_file, target_pkg_base, service_name_no_hyphen)

        # Copy Exception
        if os.path.exists(exception_source):
            for root, _, files in os.walk(exception_source):
                for file in files:
                    if file.endswith('.java'):
                        source_file = os.path.join(root, file)
                        rel_path = os.path.relpath(source_file, os.path.join(SOURCE_BASE)) # relative to musicplatform
                        dest_file = os.path.join(DEST_BASE, target_service, "src", "main", "java", target_pkg_base.replace('.', os.sep), rel_path.split(os.sep, 1)[1])
                        process_file(source_file, dest_file, target_pkg_base, service_name_no_hyphen)

    print("Porting strictly missing classes complete.")

if __name__ == "__main__":
    main()
