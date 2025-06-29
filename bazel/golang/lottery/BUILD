load("@gazelle//:def.bzl", "gazelle")
load("@rules_go//go:def.bzl", "go_binary", "go_library")

gazelle(name = "gazelle")

# load("@io_bazel_rules_go//go:def.bzl", "go_binary")

# gazelle:prefix EdColeman/cuddly-chainsaw/bazel/golang/lottery/hello
# gazelle:build_file_name BUILD,BUILD.bazel

go_library(
    name = "a_lib",
    srcs = ["hello.go"],
    importpath = "github.com:EdColeman/cuddly-chainsaw/bazel/golang/lottery/hello",
    visibility = ["//visibility:private"],
)

go_binary(
    name = "hello",
    embed = [":hello_lib"],
    visibility = ["//visibility:public"],
)

go_library(
    name = "hello_lib",
    srcs = ["hello.go"],
    importpath = "EdColeman/cuddly-chainsaw/bazel/golang/lottery/hello",
    visibility = ["//visibility:private"],
)
