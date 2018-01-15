package com.refrii.client.views.adapters

import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log

class FoodListSwipeCallback(dragDir: Int, swipeDir: Int, private val adapter: FoodRecyclerViewAdapter) : ItemTouchHelper.SimpleCallback(dragDir, swipeDir) {
    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
//        val fromPos = viewHolder?.adapterPosition
//        val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter


//                Log.e(TAG, viewHolder?.adapterPosition.toString())
//                val food = adapter.foods[fromPos!!]
//                Log.e(TAG, food.name)
//
//                mRealm.executeTransaction {
//                    adapter.remove(food)
//                    adapter.notifyDataSetChanged()
//                }
//
//                Snackbar.make(mRecyclerView, "test", Snackbar.LENGTH_LONG)
//                        .setAction("Undo") {
//                            mRealm.executeTransaction {
//                                mRealm.copyToRealmOrUpdate(food)
//                                adapter.add(food)
//                            }
//                            adapter.notifyDataSetChanged()
//                        }
//                        .show()
    }

    override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
//                val itemView = viewHolder.itemView
//                val itemHeight = itemView.bottom - itemView.top
//                val deleteIcon = ContextCompat.getDrawable(this@BoxActivity, R.drawable.ic_account_box_black)
//                val intrinsicWidth = deleteIcon.intrinsicWidth
//                val intrinsicHeight = deleteIcon.intrinsicHeight
//                val background = ColorDrawable()
//                val backgroundColor = Color.parseColor("#f44336")
//
//                // Draw the red delete background
//                background.color = backgroundColor
//                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
//                background.draw(c)
//
//                // Calculate position of delete icon
//                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
//                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
//                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
//                val deleteIconRight = itemView.right - deleteIconMargin
//                val deleteIconBottom = deleteIconTop + intrinsicHeight
//
//                // Draw the delete icon
//                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
//                deleteIcon.draw(c)
//
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        recyclerView?.setOnTouchListener { v, event ->
            false
        }

        val icon: Bitmap
        val p = Paint()
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView
            val height = itemView.bottom - itemView.top
            val width = height / 3

            if (dX > 0) {
                p.color = Color.parseColor("#388E3C")
                val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                c?.drawRect(background, p)
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_box_black);
                val icon_dest = RectF(itemView.getLeft().toFloat() + width, itemView.getTop().toFloat() + width, itemView.getLeft() + 2 * width.toFloat(), itemView.getBottom().toFloat() - width);
//                        c?.drawBitmap(icon,null,icon_dest,p);
            } else {
                p.color = Color.parseColor("#D32F2F")
                val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                c?.drawRect(background, p)
//                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_add_circle_outline_black);
                val icon_dest = RectF(itemView.getRight().toFloat() - 2 * width, itemView.getTop().toFloat() + width, itemView.getRight() - width.toFloat(), itemView.getBottom().toFloat() - width);
//                        c?.drawBitmap(icon,null,icon_dest,p);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        Log.e("wwwwwwwwwwwwwwww", "Wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww")
//        return 0
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }
}