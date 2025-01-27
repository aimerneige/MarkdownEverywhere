package main

import (
	"fmt"

	"github.com/gin-gonic/gin"
)

func main() {
	conf := readConf()
	if conf == nil {
		fmt.Println("conf read failed")
		return
	}
	// fmt.Println(c)
	// setRepoPath(conf.["git"].(string))
	setRepoPath((*conf)["git"].(string))
	// firstInit()
	cloneRepo()
	pullRepo()
	genRepoTree()
	r := gin.Default()
	r.GET("/ping", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"message": "pong",
		})
	})
	SQL.InitDB()
	API.CreateGetArticleApi(r)
	API.CreateGetListApi(r)
	API.CreateUpdateHookApi(r)
	API.CreateAddCommentAPI(r)
	API.CreateGetCommentsNextPageAPI(r)
	API.CreateGetCommentsPreAPI(r)
	API.CreateGetCommentsPageLatestAPI(r)

	r.Run((*conf)["ip"].(string)) // listen and serve on 0.0.0.0:8080 (for windows "localhost:8080")
}
