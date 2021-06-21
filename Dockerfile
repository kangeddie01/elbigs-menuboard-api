name: CI/CD Docker

on:
  push:
    branches: [ master, develop ] # push할 때 동작할 branch를 입력해주세요.
env:
  DOCKER_IMAGE: ghcr.io/kangeddie01/menuboard
  VERSION: ${{ github.sha }}

jobs:
  build:
    name: Build
    runs-on: ubuntu-latest
    steps:
      - name: Check out source code
        uses: actions/checkout@v2
      - name: Set up docker buildx
        id: buildx
        uses: docker/setup-buildx-action@v1
      - name: Cache docker layers
        uses: actions/cache@v2
        with:
          path: /tmp/.buildx-cache
          key: ${{ runner.os }}-buildx-${{ env.VERSION }} # runner 설정에서 읽어들일거에요.
          restore-keys: |
            ${{ runner.os }}-buildx-
      - name: Login to ghcr
        uses: docker/login-action@v1
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.CR_PAT }}
      - name: Build and push
        id: docker_build
        uses: docker/build-push-action@v2
        with:
          builder: ${{ steps.buildx.outputs.name }}
          push: ${{ github.event_name != 'pull_request' }}
          tags: ${{ env.DOCKER_IMAGE }}:${{ env.VERSION }}

  deploy:
    needs: build
    name: Deploy
    runs-on: [ self-hosted, dev-runner ]
    steps:
      - name: Login to ghcr
        uses: docker/login-action@v1
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.CR_PAT }}
      - name: Docker run
        run: |
          docker ps -q --filter "name=menuboard" | grep -q . && docker stop menuboard && docker rm -fv menuboard
          docker run -d -p 8080:8080 --name cicd --restart always ${{ env.DOCKER_IMAGE }}:${{ env.VERSION }}
