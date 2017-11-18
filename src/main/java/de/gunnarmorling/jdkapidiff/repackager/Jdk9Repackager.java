/**
 *  Copyright 2017 Gunnar Morling (http://www.gunnarmorling.de/)
 *  and/or other contributors as indicated by the @authors tag. See the
 *  copyright.txt file in the distribution for a full listing of all
 *  contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.gunnarmorling.jdkapidiff.repackager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.spi.ToolProvider;

class Jdk9Repackager extends JdkRepackager {

    public Jdk9Repackager(Path javaHome, String version) {
        super( javaHome, version );
    }

    @Override
    public void extractJdkClasses(Path targetDir) throws IOException {
        Optional<ToolProvider> jmod = ToolProvider.findFirst( "jmod" );
        if ( !jmod.isPresent() ) {
            throw new IllegalStateException( "Couldn't find jmod tool" );
        }

        Files.list( javaHome.resolve( "jmods" ) )
            .filter( p -> !p.getFileName().toString().startsWith( "jdk.internal") )
            .forEach( module -> {
                System.out.println( "Extracting module " + module );
                jmod.get().run( System.out, System.err, "extract", "--dir", targetDir.toString(), module.toString() );
            });

        Files.delete( targetDir.resolve( "classes" ).resolve( "module-info.class") );
    }
}
