{
  description = "scala-stakeholder deterministic tranche";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.11";
  outputs = { self, nixpkgs }:
    let
      systems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];
      forAllSystems = f: builtins.listToAttrs (map (system: { name = system; value = f system; }) systems);
    in {
      packages = forAllSystems (system:
        let pkgs = import nixpkgs { inherit system; };
        in {
          check = pkgs.writeShellApplication {
            name = "check";
            runtimeInputs = [ pkgs.python3 pkgs.sbt pkgs.jdk21 ];
            text = ''
              python3 scripts/validate_scaffold.py
              sbt test stage
            '';
          };
          default = self.packages.${system}.check;
        });
      apps = forAllSystems (system: {
        check = { type = "app"; program = "${self.packages.${system}.check}/bin/check"; };
        default = self.apps.${system}.check;
      });
    };
}
