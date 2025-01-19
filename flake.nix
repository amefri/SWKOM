{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.11";
    systems.url = "github:nix-systems/default";
    devenv.url = "github:cachix/devenv";
    devenv.inputs.nixpkgs.follows = "nixpkgs";
  };

  nixConfig = {
    extra-trusted-public-keys = "devenv.cachix.org-1:w1cLUi8dv3hnoSPGAuibQv+f9TZLr6cv/Hm9XgU50cw=";
    extra-substituters = "https://devenv.cachix.org";
  };

  outputs = { self, nixpkgs, devenv, systems, ... } @ inputs:
    let
      forEachSystem = nixpkgs.lib.genAttrs (import systems);
    in
    {
      packages = forEachSystem (system: {
        devenv-up = self.devShells.${system}.default.config.procfileScript;
      });

      devShells = forEachSystem
        (system:
          let
            pkgs = nixpkgs.legacyPackages.${system};
          in
          {
            default = devenv.lib.mkShell {
              inherit inputs pkgs;
              modules = [
                {
                  languages.python = {
                    enable = true;
                    package =pkgs.python312;
                    uv.enable = true;
                  };
                  languages.clojure = {
                    enable = true;
                  };
                  languages.java = {
                    enable = true;
                    jdk.package = pkgs.openjdk;
                    maven.enable = true;
                    gradle.enable = true;
                    gradle.package = pkgs.gradle;
                  };
                  languages.javascript = {
                    enable = true;
                    bun = {
                      enable = true;
                      install.enable = true;
                    };
                    corepack.enable = true;
                    npm.enable = true;
                  };
                  difftastic.enable = true;
                  dotenv.enable = true;

                  # https://devenv.sh/reference/options/
                  packages = with pkgs; [ 
                    #podman-compose
                    docker-compose
                    pgcli
                    openapi-generator-cli
                    python312Packages.pyyaml
                    minio
                    leiningen
                    tesseract
                    rabbitmq-server
                  ];

                  enterShell = ''
                    source ./.env
                    java --version
                  '';

                  processes.hello.exec = "hello";
                }
              ];
            };
          });
    };
}
