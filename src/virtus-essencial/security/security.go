package security

import (
	"net/http"

	"github.com/gorilla/sessions"
)

var CookieName = "virtus"
var Store = sessions.NewCookieStore([]byte("vindixit123581321"))

func IsAuthenticated(w http.ResponseWriter, r *http.Request) bool {
	session, err := Store.Get(r, CookieName)
	if err != nil {
		return false
	}
	sessionUser := session.Values["user"]
	if sessionUser == nil {
		return false
	}
	return true
}
